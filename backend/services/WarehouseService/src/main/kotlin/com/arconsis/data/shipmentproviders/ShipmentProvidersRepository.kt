package com.arconsis.data.shipmentproviders

import com.arconsis.common.body
import com.arconsis.common.bodyAsString
import com.arconsis.common.errors.abort
import com.arconsis.common.pmap
import com.arconsis.data.shipmentproviders.dto.*
import com.arconsis.domain.shipmentproviders.ShipmentProvider
import com.arconsis.domain.shipmentproviders.ShipmentProviderRate
import com.arconsis.domain.shipmentproviders.ShipmentProvidersFailureReason
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.BadRequestException

@ApplicationScoped
class ShipmentProvidersRepository(
	@RestClient private val shipmentProvidesRemoteStore: ShipmentProvidesRemoteStore,
	private val objectMapper: ObjectMapper
) {
	suspend fun listShipmentProviders(): List<ShipmentProvider> {
		val serviceGroupsResponse = shipmentProvidesRemoteStore.listServiceGroups().awaitSuspending()
		val serviceGroups = when (serviceGroupsResponse.status) {
			in 200..299 -> {
				objectMapper.readValue(serviceGroupsResponse.bodyAsString(), object: TypeReference<List<ServiceGroupResponseDto>>() {})
					?.filter {
						it.isActive
					}
			}
			else -> throw BadRequestException("Service group not found")
		} ?: return emptyList()
		return serviceGroups.pmap { serviceGroup ->
			val providerId = serviceGroup.serviceLevels[0].carrierAccount
			val carrierAccountResponse = shipmentProvidesRemoteStore.getCarrierAccount(providerId, SERVICE_LEVEL_QUERY_PARAM)
				.awaitSuspending()
			val carrierAccount = when (carrierAccountResponse.status) {
				in 200..299 -> carrierAccountResponse.body<ShipmentProviderResponseDto>()!!
				else -> throw BadRequestException("Carrier account not found")
			}
			ShipmentProvider(
				providerId = carrierAccount.providerId,
				name = serviceGroup.name,
				description = serviceGroup.description,
				rate = ShipmentProviderRate(
					price = serviceGroup.price,
					currency = serviceGroup.currency,
				),
				carrier = carrierAccount.carrier,
				carrierName = carrierAccount.carrierName,
				carrierImage = carrierAccount.carrierImages.image200,
				carrierAccount = serviceGroup.serviceLevels[0].token,
			)
		}
	}

	fun getShipmentId(providerId: String): Uni<String?> {
		return shipmentProvidesRemoteStore.getShipmentProviderRate(providerId).map {
			when (it.status) {
				in 200..299 -> it.body<ShipmentProviderRateResponseDto>()?.shipmentId
				else -> abort(ShipmentProvidersFailureReason.ShipmentProviderNotFound)
			}
		}
	}

	fun getShipmentProviderToken(carrierAccountId: String, carrierName: String): Uni<String?> {
		return shipmentProvidesRemoteStore.getCarrierAccount(carrierAccountId, SERVICE_LEVEL_QUERY_PARAM)
			.map { response ->
				when (response.status) {
					in 200..299 -> {
						response.body<ShipmentProviderResponseDto>()
							?.serviceLevels
							?.firstOrNull { it.carrierName == carrierName }
							?.token

					}
					else -> abort(ShipmentProvidersFailureReason.ShipmentProviderNotFound)
				}
			}
	}

	companion object {
		private const val SERVICE_LEVEL_QUERY_PARAM = 1
	}
}
