package com.arconsis.domain.orders

import com.arconsis.domain.addresses.Address
import com.arconsis.domain.checkouts.Checkout
import com.arconsis.domain.checkouts.CheckoutStatus
import com.arconsis.domain.checkouts.CreateCheckout
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
    val checkout: Checkout?,
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

fun Order.toCreateCheckout(checkoutSessionId: String, checkoutUrl: String, status: CheckoutStatus) = CreateCheckout(
    userId = userId,
    orderId = orderId,
    amount = totalPrice,
    currency = currency,
    status = status,
    checkoutSessionId = checkoutSessionId,
    checkoutUrl = checkoutUrl
)

class OrdersDeserializer : ObjectMapperDeserializer<Order>(Order::class.java)