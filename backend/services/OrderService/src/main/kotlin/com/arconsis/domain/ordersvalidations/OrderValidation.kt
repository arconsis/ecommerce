package com.arconsis.domain.ordersvalidations

import com.arconsis.domain.orderitems.OrderItem
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.util.*

data class OrderValidation(
    val orderId: UUID,
    val userId: UUID,
    val status: OrderValidationStatus,
    val items: List<OrderItem>
)

enum class OrderValidationStatus {
    VALIDATED,
    INVALID
}

class OrderValidationDeserializer :
    ObjectMapperDeserializer<OrderValidation>(OrderValidation::class.java)