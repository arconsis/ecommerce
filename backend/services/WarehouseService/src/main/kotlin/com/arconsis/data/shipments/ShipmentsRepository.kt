package com.arconsis.data.shipments

import com.arconsis.domain.shipments.CreateShipment
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.UpdateShipment
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsRepository(private val shipmentsDataStore: ShipmentsDataStore) {

    fun createShipment(createShipment: CreateShipment): Uni<Shipment> {
        return shipmentsDataStore.createShipment(createShipment)
    }

    fun updateShipmentStatus(updateShipment: UpdateShipment): Uni<Shipment> {
        return shipmentsDataStore.updateShipmentStatus(updateShipment.id, updateShipment.status)
    }
}