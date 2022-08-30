package com.arconsis.data.shipments

import com.arconsis.domain.shipments.CreateShipment
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentStatus
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import org.jboss.logging.Logger
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsRepository(
	private val shipmentsDataStore: ShipmentsDataStore,
	private val logger: Logger,
) {
	fun createShipment(newShipment: CreateShipment, session: Mutiny.Session): Uni<Shipment> {
		return shipmentsDataStore.createShipment(newShipment, ShipmentStatus.PREPARING_SHIPMENT, session)
			.map {
				it
			}
	}

	fun updateShipmentStatus(shipmentId: UUID, shipmentStatus: ShipmentStatus, session: Mutiny.Session): Uni<Shipment> {
		return shipmentsDataStore.updateShipmentStatus(shipmentId, shipmentStatus, session)
	}
}
