package com.arconsis.domain.shipmentproviders

import com.arconsis.data.shipmentproviders.ShipmentProvidersRepository
import com.arconsis.presentation.http.shipments.dto.DeliveryAddressDto
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentProvidersService(
    private val shipmentProvidersRepository: ShipmentProvidersRepository
) {
    suspend fun listShipmentProviders(deliveryAddress: DeliveryAddressDto): List<ShipmentProvider> {
        return shipmentProvidersRepository.listShipmentProviders(deliveryAddress)
    }
}