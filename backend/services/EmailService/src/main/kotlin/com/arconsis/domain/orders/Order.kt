package com.arconsis.domain.orders

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import com.arconsis.domain.addresses
import java.util.*

data class Order(
    val orderId: UUID,
    val basketId: UUID,
    val userId: UUID,
    val prices: OrderPrices,
    val status: OrderStatus,
    val items: List<OrderItem>,
    // addresses
    val shippingAddress: Address? = null,
    val billingAddress: Address? = null,
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

data class OrderPrices (
    val totalPrice: BigDecimal,
    val tax: String,
    val priceBeforeTax: BigDecimal,
    val currency: String,
)

class OrdersDeserializer : ObjectMapperDeserializer<Order>(Order::class.java)