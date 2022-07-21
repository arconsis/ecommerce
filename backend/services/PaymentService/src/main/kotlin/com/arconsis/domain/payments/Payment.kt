package com.arconsis.domain.payments

import com.arconsis.domain.checkouts.Checkout
import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.outboxevents.OutboxEventType
import com.fasterxml.jackson.databind.ObjectMapper
import java.math.BigDecimal
import java.util.*

data class Payment(
    val paymentId: UUID?,
    val transactionId: UUID?,
    val orderId: UUID,
    val userId: UUID,
    val amount: BigDecimal,
    val currency: String,
    val status: PaymentStatus,
    val checkout: Checkout
)

enum class PaymentStatus {
    IN_PROGRESS,
    SUCCEED,
    FAILED,
}

data class CreatePayment(
    val orderId: UUID,
    val userId: UUID,
    val amount: BigDecimal,
    val currency: String,
    val checkoutSessionId: String,
    val checkoutUrl: String
)

fun Payment.toCreateOutboxEvent(objectMapper: ObjectMapper): CreateOutboxEvent = CreateOutboxEvent(
    aggregateType = AggregateType.PAYMENT,
    aggregateId = this.paymentId!!,
    type = this.status.toOutboxEventType(),
    payload = objectMapper.writeValueAsString(this)
)

private fun PaymentStatus.toOutboxEventType(): OutboxEventType = when (this) {
    PaymentStatus.SUCCEED -> OutboxEventType.PAYMENT_SUCCEED
    PaymentStatus.FAILED -> OutboxEventType.PAYMENT_FAILED
    PaymentStatus.IN_PROGRESS -> OutboxEventType.PAYMENT_IN_PROGRESS
}