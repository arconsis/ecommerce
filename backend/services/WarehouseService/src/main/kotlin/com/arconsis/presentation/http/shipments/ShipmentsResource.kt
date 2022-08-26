package com.arconsis.presentation.http.shipments

import com.arconsis.domain.shipmentproviders.ShipmentProvider
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentsService
import com.arconsis.domain.shipments.UpdateShipment
import com.arconsis.presentation.http.shipments.dto.DeliveryAddressDto
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*

@ApplicationScoped
@Path("/shipments")
class ShipmentsResource(private val shipmentsService: ShipmentsService) {
    @POST
    @Path("/providers")
    suspend fun listShipmentProviders(deliveryAddress: DeliveryAddressDto): List<ShipmentProvider> {
        return shipmentsService.listShipmentProviders(deliveryAddress)
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