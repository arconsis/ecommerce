package com.arconsis.data.shipments

import com.arconsis.common.body
import com.arconsis.common.bodyAsString
import com.arconsis.data.shipments.dto.ShipmentResponseDto
import com.arconsis.domain.shipments.CreateShipment
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.ShipmentStatus
import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.hibernate.reactive.mutiny.Mutiny
import org.jboss.logging.Logger
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsRepository(
	private val shipmentsDataStore: ShipmentsDataStore,
	@RestClient private val shipmentsRemoteStore: ShipmentsRemoteStore,
	private val logger: Logger,
) {
	fun createShipment(createShipment: CreateShipment, session: Mutiny.Session): Uni<Shipment> {
		return createShipmentOnShippo(createShipment)
			.onFailure()
			.recoverWithNull()
			.flatMap { externalShipment ->
				if (externalShipment != null) {
					val newShipment = createShipment.copy(externalShipmentId = externalShipment.externalShipmentId)
					shipmentsDataStore.createShipment(newShipment, ShipmentStatus.PREPARING_SHIPMENT, session)
				} else {
					logger.error("Creating shipment failed for order with id: ${createShipment.orderId} because of externalShipment not found")
					val newShipment = createShipment.copy(externalShipmentId = null)
					shipmentsDataStore.createShipment(newShipment, ShipmentStatus.CREATING_SHIPMENT_LABEL_FAILED, session)
				}
			}
			.map {
				it
			}
	}

	private fun createShipmentOnShippo(createShipment: CreateShipment): Uni<ShipmentResponseDto?> {
		return shipmentsRemoteStore.createShipment(
			providerId = createShipment.externalShipmentProviderId,
			labelFileType = "PDF",
			async = "false"
		).map {
			when (it.status) {
				in 200..299 -> {
					it.body<ShipmentResponseDto>()
				}
				else -> {
					logger.error("Creating shipment failed for order with id: ${createShipment.orderId} because of ${it.bodyAsString()}")
					null
				}
			}
		}.onFailure {
			logger.error("Creating shipment failed for order with id: ${createShipment.orderId} because of ${it}")
			false
		}.recoverWithNull()
	}

	fun updateShipmentStatus(shipmentId: UUID, shipmentStatus: ShipmentStatus, session: Mutiny.Session): Uni<Shipment> {
		return shipmentsDataStore.updateShipmentStatus(shipmentId, shipmentStatus, session)
	}
}
