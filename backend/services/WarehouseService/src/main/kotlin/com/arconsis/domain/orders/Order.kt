package com.arconsis.domain.orders

import com.arconsis.domain.addresses.Address
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer
import java.math.BigDecimal
import java.util.*

data class Order(
    val orderId: UUID,
    val basketId: UUID,
    val userId: UUID,
    val totalPrice: BigDecimal,
    val tax: String,
    val priceBeforeTax: BigDecimal,
    val currency: String,
    val status: OrderStatus,
    val items: List<OrderItem>,
    // addresses
    val shippingAddress: Address? = null,
    val billingAddress: Address? = null,
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