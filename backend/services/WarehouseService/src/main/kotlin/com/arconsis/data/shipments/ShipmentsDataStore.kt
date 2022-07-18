package com.arconsis.data.shipments

import com.arconsis.data.common.asPair
import com.arconsis.data.common.toUni
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

	private fun getShipment(id: UUID): Uni<Shipment?> {
		return find("id", id)
			.firstResult<ShipmentEntity?>()
			.map {
				it.toShipment()
			}
	}

	fun updateShipmentStatus(id: UUID, status: ShipmentStatus): Uni<Shipment> {
		val params: MutableMap<String, Any> = HashMap()
		params["status"] = status
		return update("id", id, params).map {
			it > 0
		}.flatMap { result ->
			Uni.combine().all().unis(
				getShipment(id),
				result.toUni(),
			).asPair()
		}.map { (shipment, _) ->
			shipment
		}
	}
}