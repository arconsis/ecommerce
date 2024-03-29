package com.arconsis.domain.outboxevents

import java.util.*

data class CreateOutboxEvent(
    val aggregateType: AggregateType,
    val aggregateId: UUID,
    val payload: String,
    val type: OutboxEventType
)

data class OutboxEvent(
    val id: UUID,
    val aggregateType: AggregateType,
    val aggregateId: UUID,
    val type: OutboxEventType,
    val payload: String,
)

enum class AggregateType {
    ORDER,
    CHECKOUT,
}

enum class OutboxEventType {
    // payments
    PAYMENT_IN_PROGRESS,
    PAYMENT_SUCCEED,
    PAYMENT_FAILED,
    // orders
    ORDER_REQUESTED,
    ORDER_VALIDATED,
    ORDER_OUT_OF_STOCK,
    ORDER_PAYMENT_IN_PROGRESS,
    ORDER_PAID,
    ORDER_SHIPPED,
    ORDER_COMPLETED,
    ORDER_PAYMENT_FAILED,
    ORDER_CANCELLED,
    ORDER_REFUNDED,
    ORDER_SHIPMENT_PREPARING_SHIPMENT,
    ORDER_SHIPMENT_DELIVERY_FAILED,
    ORDER_CREATING_SHIPMENT_LABEL_FAILED
}