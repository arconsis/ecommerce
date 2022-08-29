package com.arconsis.data.shipmentproviders

import com.arconsis.common.body
import com.arconsis.data.shipmentproviders.dto.*
import com.arconsis.domain.shipmentproviders.ShipmentProvider
import com.arconsis.presentation.http.shipments.dto.DeliveryAddressDto
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.BadRequestException

@ApplicationScoped
class ShipmentProvidersRepository(
    @RestClient private val shipmentProvidesRemoteStore: ShipmentProvidesRemoteStore,
) {
    suspend fun listShipmentProviders(deliveryAddress: DeliveryAddressDto): List<ShipmentProvider> {
        val response =  shipmentProvidesRemoteStore.listShipmentProviders(
            ListShipmentProvidersDto(
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

        )
        )
        return when (response.status) {
            in 200..299 -> response.body<ShipmentProviderResponseDto>()!!.providers.map {
                it.toShipmentProvider()
            }
            else -> throw BadRequestException("Shipment providers not found")
        }
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
