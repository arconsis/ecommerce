package com.arconsis.data.shippingproviders

import com.arconsis.common.body
import com.arconsis.common.bodyAsString
import com.arconsis.common.errors.abort
import com.arconsis.common.pmap
import com.arconsis.data.shippingproviders.dto.*
import com.arconsis.domain.shippingproviders.ShippingProvider
import com.arconsis.domain.shippingproviders.ShippingProviderRate
import com.arconsis.domain.shippingproviders.ShippingProvidersFailureReason
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.BadRequestException

@ApplicationScoped
class ShippingProvidersRepository(
	@RestClient private val shippingProvidesRemoteStore: ShippingProvidesRemoteStore,
	private val objectMapper: ObjectMapper
) {
	suspend fun listShippingProviders(): List<ShippingProvider> {
		val serviceGroupsResponse = shippingProvidesRemoteStore.listServiceGroups().awaitSuspending()
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
			val carrierAccountResponse = shippingProvidesRemoteStore.getCarrierAccount(providerId, SERVICE_LEVEL_QUERY_PARAM)
				.awaitSuspending()
			val carrierAccount = when (carrierAccountResponse.status) {
				in 200..299 -> carrierAccountResponse.body<ShippingProviderResponseDto>()!!
				else -> throw BadRequestException("Carrier account not found")
			}
			ShippingProvider(
				providerId = carrierAccount.providerId,
				name = serviceGroup.name,
				description = serviceGroup.description,
				rate = ShippingProviderRate(
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
		return shippingProvidesRemoteStore.getShippingProviderRate(providerId).map {
			when (it.status) {
				in 200..299 -> it.body<ShippingProviderRateResponseDto>()?.shipmentId
				else -> abort(ShippingProvidersFailureReason.ShippingProviderNotFound)
			}
		}
	}

	fun getShippingProviderToken(carrierAccountId: String, carrierName: String): Uni<String?> {
		return shippingProvidesRemoteStore.getCarrierAccount(carrierAccountId, SERVICE_LEVEL_QUERY_PARAM)
			.map { response ->
				when (response.status) {
					in 200..299 -> {
						response.body<ShippingProviderResponseDto>()
							?.serviceLevels
							?.firstOrNull { it.carrierName == carrierName }
							?.token

					}
					else -> abort(ShippingProvidersFailureReason.ShippingProviderNotFound)
				}
			}
	}

	companion object {
		private const val SERVICE_LEVEL_QUERY_PARAM = 1
	}
}
