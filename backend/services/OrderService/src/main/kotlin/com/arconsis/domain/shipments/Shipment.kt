package com.arconsis.domain.shipments

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.util.*

data class Shipment(
    val shipmentId: UUID,
    val orderId: UUID,
    val userId: UUID,
    val status: ShipmentStatus,
)

enum class ShipmentStatus {
    PREPARING_SHIPMENT,
    SHIPPED,
    DELIVERED,
    CANCELLED,
}

val Shipment.isOutForShipment
    get() = status == ShipmentStatus.SHIPPED

class ShipmentDeserializer : ObjectMapperDeserializer<Shipment>(Shipment::class.java)