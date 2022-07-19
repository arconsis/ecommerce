package com.arconsis.domain.orders

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.util.*

data class Order(
    val orderId: UUID,
    val userId: UUID,
    val amount: BigDecimal,
    val currency: String,
    val status: OrderStatus,
    val items: List<OrderItem>
)

enum class OrderStatus {
    REQUESTED,
    VALIDATED,
    OUT_OF_STOCK,
    PAID,
    SHIPPED,
    COMPLETED,
    PAYMENT_FAILED,
    CANCELLED,
    REFUNDED
}

class OrdersDeserializer : ObjectMapperDeserializer<Order>(Order::class.java)