package com.arconsis.data.shipments

import com.arconsis.domain.shipments.CreateShipment
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentStatus
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsRepository(private val shipmentsDataStore: ShipmentsDataStore) {

    fun createShipment(createShipment: CreateShipment, session: Mutiny.Session): Uni<Shipment> {
        return shipmentsDataStore.createShipment(createShipment, session)
    }

    fun updateShipmentStatus(shipmentId: UUID, shipmentStatus: ShipmentStatus, session: Mutiny.Session): Uni<Shipment> {
        return shipmentsDataStore.updateShipmentStatus(shipmentId, shipmentStatus, session)
    }
}