package com.arconsis.data.shipments

import com.arconsis.common.body
import com.arconsis.data.shipments.dto.*
import com.arconsis.domain.shipmentproviders.ShipmentProvider
import com.arconsis.domain.shipments.CreateShipment
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentStatus
import com.arconsis.presentation.http.shipments.dto.DeliveryAddressDto
import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.hibernate.reactive.mutiny.Mutiny
import java.util.UUID
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.BadRequestException

@ApplicationScoped
class ShipmentsRepository(
    @RestClient private val shipmentsRemoteStore: ShipmentsRemoteStore,
    private val shipmentsDataStore: ShipmentsDataStore,
) {

    fun createShipment(createShipment: CreateShipment, session: Mutiny.Session): Uni<Shipment> {
        return shipmentsDataStore.createShipment(createShipment, session)
    }

    fun updateShipmentStatus(shipmentId: UUID, shipmentStatus: ShipmentStatus, session: Mutiny.Session): Uni<Shipment> {
        return shipmentsDataStore.updateShipmentStatus(shipmentId, shipmentStatus, session)
    }

    suspend fun listShipmentProviders(deliveryAddress: DeliveryAddressDto): List<ShipmentProvider> {
        val response =  shipmentsRemoteStore.listShipmentProviders(ListShipmentProvidersDto(
            addressFrom = ShipmentAddressDto(
                name = fromName,
                street1 = fromStreet,
                city = fromCity,
                zip = fromPostalCode,
                country = fromCountry
            ),
            addressTo = ShipmentAddressDto(
                name = deliveryAddress.name,
                street1 = "${deliveryAddress.address} ${deliveryAddress.houseNumber}",
                city = deliveryAddress.city,
                zip = deliveryAddress.postalCode,
                country = deliveryAddress.countryCode
            ),
            parcels = listOf(ParcelDto())

        ))
        return when (response.status) {
            in 200..201 -> response.body<ShipmentProviderResponseDto>()!!.providers.map {
                it.toShipmentProvider()
            }
            else -> throw BadRequestException("Shipment providers not found")
        }
    }

    companion object {
        private const val fromName = "Arconsis Ecommerce"
        private const val fromStreet = "Krithoni 9"
        private const val fromCity = "Berlin"
        private const val fromPostalCode = "10115"
        private const val fromCountry = "DE"
    }
}
