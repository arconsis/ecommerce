package com.arconsis.domain.shippingproviders

import com.arconsis.data.shippingproviders.ShippingProvidersRepository
import com.arconsis.presentation.http.shipments.request.dto.DeliveryAddressDto
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShippingProvidersService(
    private val shippingProvidersRepository: ShippingProvidersRepository
) {
    suspend fun listShippingProviders(deliveryAddress: DeliveryAddressDto): List<ShippingProvider> {
        return shippingProvidersRepository.listShippingProviders()
    }
}