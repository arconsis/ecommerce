package com.arconsis.data.shipmentlabels

import com.arconsis.common.body
import com.arconsis.common.bodyAsString
import com.arconsis.data.shipmentlabels.dto.request.CreateShipmentDto
import com.arconsis.data.shipmentlabels.dto.request.CreateShipmentLabelDto
import com.arconsis.data.shipmentlabels.dto.request.ParcelDto
import com.arconsis.data.shipmentlabels.dto.response.ShipmentLabelResponseDto
import com.arconsis.data.shipmentlabels.dto.request.ShipmentAddressDto
import com.arconsis.domain.shippingaddresses.ShippingAddress
import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentLabelsRepository(
	@RestClient private val shipmentsRemoteStore: ShipmentLabelsRemoteStore,
	private val logger: Logger,
) {
	fun createShipmentLabel(
		deliveryAddress: ShippingAddress,
		externalShippingProviderId: String,
		serviceLevelToken: String,
		orderId: UUID,
	): Uni<Pair<String?, String?>> {
		return createShipmentLabelOnShippo(deliveryAddress, externalShippingProviderId, serviceLevelToken, orderId)
			.onFailure()
			.recoverWithNull()
			.map {
				it?.externalShipmentLabelId to it?.rate?.externalRateId
			}
	}

	private fun createShipmentLabelOnShippo(
		deliveryAddress: ShippingAddress,
		externalShippingProviderId: String,
		serviceLevelToken: String,
		orderId: UUID,
	): Uni<ShipmentLabelResponseDto?> {
		val createShipmentLabelDto = CreateShipmentLabelDto(
			shipment = CreateShipmentDto(
				addressFrom = ShipmentAddressDto(
					name = fromName,
					street1 = fromStreet,
					city = fromCity,
					zip = fromPostalCode,
					country = fromCountry,
					state = fromState
				),
				addressTo = ShipmentAddressDto(
					name = "${deliveryAddress.firstName} ${deliveryAddress.lastName}",
					street1 = "${deliveryAddress.houseNumber} ${deliveryAddress.address}",
					city = deliveryAddress.city,
					zip = deliveryAddress.postalCode,
					country = deliveryAddress.countryCode,
					state = deliveryAddress.state
				),
				parcels = listOf(ParcelDto())
			),
			providerId = externalShippingProviderId,
			serviceLevelToken = serviceLevelToken
		)
		return shipmentsRemoteStore.createShipmentLabel(createShipmentLabelDto)
			.map {
				when (it.status) {
					in 200..299 -> {
						it.body<ShipmentLabelResponseDto>()
					}
					else -> {
						logger.error("Creating shipment failed for order with id: $orderId because of ${it.bodyAsString()}")
						null
					}
				}
			}.onFailure {
				logger.error("Creating shipment failed for order with id: $orderId because of $it")
				false
			}.recoverWithNull()
	}

	companion object {
		private const val fromName = "Arconsis Ecommerce"
		private const val fromStreet = "Clayton St."
		private const val fromCity = "San Francisco"
		private const val fromPostalCode = "94100"
		private const val fromCountry = "US"
		private const val fromState = "CA"
	}
}
