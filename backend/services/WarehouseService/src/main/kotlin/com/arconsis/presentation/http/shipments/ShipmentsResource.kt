package com.arconsis.presentation.http.shipments

import com.arconsis.domain.shippingproviders.ShippingProvidersService
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentsService
import com.arconsis.domain.shipments.UpdateShipment
import com.arconsis.presentation.http.shipments.request.dto.DeliveryAddressDto
import com.arconsis.presentation.http.shipments.response.dto.ShippingProviderResponseDto
import com.arconsis.presentation.http.shipments.response.dto.toShippingProviderResponseDto
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*

@ApplicationScoped
@Path("/shipments")
class ShipmentsResource(
	private val shipmentsService: ShipmentsService,
	private val shippingProvidersService: ShippingProvidersService
) {
	@POST
	@Path("/providers")
	suspend fun listShippingProviders(deliveryAddress: DeliveryAddressDto): List<ShippingProviderResponseDto> {
		return shippingProvidersService.listShippingProviders(deliveryAddress)
			.map {
				it.toShippingProviderResponseDto()
			}
	}

	@PUT
	@Path("/{id}")
	fun updateShipment(@PathParam("id") id: UUID, updateShipment: UpdateShipment): Uni<Shipment> {
		if (id != updateShipment.shipmentId) {
			throw BadRequestException("Shipment id: $id is not correct")
		}
		return shipmentsService.updateShipment(updateShipment)
	}
}