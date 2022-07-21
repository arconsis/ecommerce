package com.arconsis.domain.orders

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.math.BigDecimal
import java.util.*

data class Order(
    val orderId: UUID,
    val basketId: UUID,
    val userId: UUID,
    val amount: BigDecimal,
    val currency: String,
    val status: OrderStatus,
    val items: List<OrderItem>,
)

data class Product(
    val productId: UUID,
    val price: BigDecimal,
    val currency: String
)

enum class OrderStatus {
    REQUESTED,
    VALIDATED,
    OUT_OF_STOCK,
    PAYMENT_IN_PROGRESS,
    PAID,
    SHIPPED,
    COMPLETED,
    PAYMENT_FAILED,
    CANCELLED,
    REFUNDED
}

class OrdersDeserializer : ObjectMapperDeserializer<Order>(Order::class.java)