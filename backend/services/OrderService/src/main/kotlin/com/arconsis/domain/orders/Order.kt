package com.arconsis.domain.orders

import com.arconsis.domain.addresses.Address
import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import com.fasterxml.jackson.databind.ObjectMapper
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
    // addresses
    val shippingAddress: Address? = null,
    val billingAddress: Address? = null,
)

data class OrderItem(
    val itemId: UUID,
    val productId: UUID,
    val orderId: UUID,
    val price: BigDecimal,
    val currency: String,
    val quantity: Int,
    val productName: String,
    val thumbnail: String,
)

data class CreateOrder(
    val userId: UUID,
    val totalPrice: BigDecimal,
    val priceBeforeTax: BigDecimal,
    val tax: String,
    val currency: String,
    val basketId: UUID,
    val pspToken: String,
    val paymentMethodType: OrderPaymentMethodType
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

fun Order.toCreateOutboxEvent(objectMapper: ObjectMapper): CreateOutboxEvent = CreateOutboxEvent(
    aggregateType = AggregateType.ORDER,
    aggregateId = this.orderId,
    type = this.status.toOutboxEventType(),
    payload = objectMapper.writeValueAsString(this)
)

private fun OrderStatus.toOutboxEventType(): OutboxEventType = when(this) {
    OrderStatus.REQUESTED -> OutboxEventType.ORDER_REQUESTED
    OrderStatus.VALIDATED -> OutboxEventType.ORDER_VALIDATED
    OrderStatus.OUT_OF_STOCK -> OutboxEventType.ORDER_OUT_OF_STOCK
    OrderStatus.PAYMENT_IN_PROGRESS -> OutboxEventType.ORDER_PAYMENT_IN_PROGRESS
    OrderStatus.PAID -> OutboxEventType.ORDER_PAID
    OrderStatus.SHIPPED -> OutboxEventType.ORDER_SHIPPED
    OrderStatus.COMPLETED -> OutboxEventType.ORDER_COMPLETED
    OrderStatus.PAYMENT_FAILED -> OutboxEventType.ORDER_PAYMENT_FAILED
    OrderStatus.CANCELLED -> OutboxEventType.ORDER_CANCELLED
    OrderStatus.REFUNDED -> OutboxEventType.ORDER_REFUNDED
}