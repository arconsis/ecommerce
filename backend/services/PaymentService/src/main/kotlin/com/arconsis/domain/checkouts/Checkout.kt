package com.arconsis.domain.checkouts

import com.arconsis.domain.orders.SupportedCurrencies
import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import com.fasterxml.jackson.databind.ObjectMapper
import java.math.BigDecimal
import java.util.*

data class Checkout(
    val checkoutId: UUID,
    val orderId: UUID,
    val userId: UUID,
    val amount: BigDecimal,
    val currency: SupportedCurrencies,
    val paymentStatus: CheckoutStatus,
    val paymentErrorMessage: String?,
    val paymentErrorCode: String?,
    // psp ref
    val pspToken: String
)

data class CreateCheckout(
    val orderId: UUID,
    val userId: UUID,
    val amount: BigDecimal,
    val currency: SupportedCurrencies,
    val paymentStatus: CheckoutStatus,
    val paymentErrorMessage: String? = null,
    val paymentErrorCode: String? = null,
    // psp ref
    val pspToken: String,
)

enum class CheckoutStatus {
    PAYMENT_IN_PROGRESS,
    PAYMENT_SUCCEED,
    PAYMENT_FAILED,
}

fun Checkout.toCreateOutboxEvent(objectMapper: ObjectMapper): CreateOutboxEvent = CreateOutboxEvent(
    aggregateType = AggregateType.CHECKOUT,
    aggregateId = this.checkoutId,
    type = this.paymentStatus.toOutboxEventType(),
    payload = objectMapper.writeValueAsString(this)
)

private fun CheckoutStatus.toOutboxEventType(): OutboxEventType = when (this) {
    CheckoutStatus.PAYMENT_SUCCEED -> OutboxEventType.PAYMENT_SUCCEED
    CheckoutStatus.PAYMENT_FAILED -> OutboxEventType.PAYMENT_FAILED
    CheckoutStatus.PAYMENT_IN_PROGRESS -> OutboxEventType.PAYMENT_IN_PROGRESS
}