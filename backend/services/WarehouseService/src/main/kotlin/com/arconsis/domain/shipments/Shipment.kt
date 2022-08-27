package com.arconsis.domain.shipments

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.reactive.messaging.kafka.Record
import java.math.BigDecimal
import java.util.*

enum class ShipmentStatus {
	PREPARING_SHIPMENT,
	SHIPPED,
	DELIVERED,
	CANCELLED,
	CREATING_SHIPMENT_LABEL_FAILED,
	DELIVERY_FAILED,
}

data class Shipment(
	val shipmentId: UUID,
	val externalShipmentId: String?,
	val shipmentFailureReason: String?,
	val externalShipmentProviderId: String,
	val providerName: String,
	val price: BigDecimal,
	val currency: String,
	val orderId: UUID,
	val status: ShipmentStatus,
	val userId: UUID
)

class UpdateShipment(val shipmentId: UUID, val status: ShipmentStatus)

data class CreateShipment(
	val orderId: UUID,
	val externalShipmentId: String? = null,
	val externalShipmentProviderId: String,
	val shipmentFailureReason: String?,
	val providerName: String,
	val price: BigDecimal,
	val currency: String,
	val userId: UUID,
	val status: ShipmentStatus,
)

fun Shipment.toShipmentRecord(): Record<String, Shipment> = Record.of(
	orderId.toString(), this
)

fun Shipment.toCreateOutboxEvent(objectMapper: ObjectMapper): CreateOutboxEvent = CreateOutboxEvent(
	aggregateType = AggregateType.SHIPMENT,
	aggregateId = this.shipmentId,
	type = this.status.toOutboxEventType(),
	payload = objectMapper.writeValueAsString(this)
)

private fun ShipmentStatus.toOutboxEventType(): OutboxEventType = when (this) {
	ShipmentStatus.PREPARING_SHIPMENT -> OutboxEventType.SHIPMENT_PREPARING_SHIPMENT
	ShipmentStatus.SHIPPED -> OutboxEventType.SHIPMENT_SHIPPED
	ShipmentStatus.DELIVERED -> OutboxEventType.SHIPMENT_DELIVERED
	ShipmentStatus.CANCELLED -> OutboxEventType.SHIPMENT_CANCELLED
	ShipmentStatus.CREATING_SHIPMENT_LABEL_FAILED -> OutboxEventType.CREATING_SHIPMENT_LABEL_FAILED
	ShipmentStatus.DELIVERY_FAILED -> OutboxEventType.SHIPMENT_DELIVERY_FAILED
}