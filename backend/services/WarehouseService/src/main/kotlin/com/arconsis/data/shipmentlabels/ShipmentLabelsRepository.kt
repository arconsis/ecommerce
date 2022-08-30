package com.arconsis.data.shipmentlabels

import com.arconsis.common.body
import com.arconsis.common.bodyAsString
import com.arconsis.data.shipmentlabels.dto.ShipmentLabelResponseDto
import com.arconsis.data.shipments.ShipmentLabelsRemoteStore
import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentLabelsRepository(
	@RestClient private val shipmentsRemoteStore: ShipmentLabelsRemoteStore,
	private val logger: Logger,
) {
	fun createShipmentLabel(externalShipmentProviderId: String, orderId: UUID): Uni<String?> {
		return createShipmentLabelOnShippo(externalShipmentProviderId, orderId)
			.onFailure()
			.recoverWithNull()
			.map {
				it?.externalShipmentLabelId
			}
	}

	private fun createShipmentLabelOnShippo(externalShipmentProviderId: String, orderId: UUID): Uni<ShipmentLabelResponseDto?> {
		return shipmentsRemoteStore.createShipmentLabel(
			providerId = externalShipmentProviderId,
			labelFileType = "PDF",
			async = "false"
		).map {
			when (it.status) {
				in 200..299 -> {
					it.body<ShipmentLabelResponseDto>()
				}
				else -> {
					logger.error("Creating shipment failed for order with id: $orderId because of ${it.bodyAsString()}")
					null
				}
			}
		}.onFailure {
			logger.error("Creating shipment failed for order with id: $orderId because of $it")
			false
		}.recoverWithNull()
	}
}
