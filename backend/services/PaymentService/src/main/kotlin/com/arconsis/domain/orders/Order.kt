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
    val prices: OrderPrices,
    val status: OrderStatus,
    val items: List<OrderItem>,
    // psp ref
    val paymentMethod: OrderPaymentMethod,
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

data class OrderPaymentMethod (
    val pspToken: String,
    val paymentMethodType: OrderPaymentMethodType
)

data class OrderPrices (
    val totalPrice: BigDecimal,
    val tax: String,
    val priceBeforeTax: BigDecimal,
    val currency: String,
)

enum class OrderPaymentMethodType {
    STRIPE,
    CASH_ON_DELIVERY
}

fun Order.toCreateCheckout(pspToken: String, paymentStatus: CheckoutStatus) = CreateCheckout(
    userId = userId,
    orderId = orderId,
    amount = prices.totalPrice,
    currency = prices.currency,
    paymentStatus = paymentStatus,
    paymentErrorMessage = null,
    paymentErrorCode = null,
    pspToken = pspToken,
)

class OrdersDeserializer : ObjectMapperDeserializer<Order>(Order::class.java)