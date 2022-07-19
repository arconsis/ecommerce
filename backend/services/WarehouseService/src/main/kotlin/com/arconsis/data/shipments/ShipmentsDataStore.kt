package com.arconsis.data.shipments

import com.arconsis.domain.shipments.CreateShipment
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentStatus
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny.Session
import java.util.HashMap
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsDataStore {
	fun createShipment(createShipment: CreateShipment, session: Session): Uni<Shipment> {
		val shipmentEntity = createShipment.toShipmentEntity()
		return session.persist(shipmentEntity)
			.map { shipmentEntity.toShipment() }
	}

	private fun getShipment(shipmentId: UUID, session: Session): Uni<Shipment> {
		return session.createNamedQuery<ShipmentEntity>(ShipmentEntity.GET_BY_SHIPMENT_ID)
			.setParameter("shipmentId", shipmentId)
			.singleResultOrNull
			.map {
				it.toShipment()
			}
	}

	fun updateShipmentStatus(shipmentId: UUID, status: ShipmentStatus, session: Session): Uni<Shipment> {
		val params: MutableMap<String, Any> = HashMap()
		params["status"] = status
		params["shipmentId"] = shipmentId
		return session.createNamedQuery<ShipmentEntity>(ShipmentEntity.UPDATE_STATUS)
			.setParameter("status", status)
			.setParameter("shipmentId", shipmentId)
			.executeUpdate()
			// TODO: Check if we have concurrency issues
			.flatMap { getShipment(shipmentId, session) }
	}
}