package com.arconsis.data.shipments

import com.arconsis.domain.shipments.CreateShipment
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentStatus
import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.Uni
import java.util.HashMap
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsDataStore : PanacheRepository<ShipmentEntity> {
	fun createShipment(createShipment: CreateShipment): Uni<Shipment> {
		val shipmentEntity = createShipment.toShipmentEntity()
		return persist(shipmentEntity)
			.map {
				it.toShipment()
			}
	}

	fun getShipment(shipmentId: UUID): Uni<Shipment> {
		return find("shipmentId", shipmentId)
			.firstResult<ShipmentEntity?>()
			.map {
				it.toShipment()
			}
	}

	fun updateShipmentStatus(shipmentId: UUID, status: ShipmentStatus): Uni<Shipment> {
		val params: MutableMap<String, Any> = HashMap()
		params["status"] = status
		params["shipmentId"] = shipmentId
		return update("update orders o set o.status = :status where o.orderId = :orderId", params)
			// TODO: Check if we have concurrency issues
			.flatMap { getShipment(shipmentId) }
	}
}