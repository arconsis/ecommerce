package com.arconsis.domain.shipments

import com.arconsis.domain.orders.SupportedCurrencies
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.math.BigDecimal
import java.util.*

data class Shipment(
	val shipmentId: UUID,
	val externalShipmentId: String?,
	val shipmentFailureReason: String?,
	val externalShippingProviderId: String,
	val externalShipmentLabelId: String?,
	val providerName: String,
	val price: BigDecimal,
	val currency: SupportedCurrencies,
	val orderId: UUID,
	val status: ShipmentStatus,
	val userId: UUID
)

enum class ShipmentStatus {
	PREPARING_SHIPMENT,
	SHIPPED,
	DELIVERED,
	CANCELLED,
	CREATING_SHIPMENT_LABEL_FAILED,
	DELIVERY_FAILED,
}

val Shipment.isOutForShipment
	get() = status == ShipmentStatus.SHIPPED

class ShipmentDeserializer : ObjectMapperDeserializer<Shipment>(Shipment::class.java)