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
    SHIPMENT,
    ORDER_VALIDATION,
    PRODUCT,
}

enum class OutboxEventType {
    // order
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
    ORDER_INVALID,
    ORDER_CREATING_SHIPMENT_LABEL_FAILED,
    ORDER_SHIPMENT_DELIVERY_FAILED,
    ORDER_SHIPMENT_PREPARING_SHIPMENT,
    // shipment
    SHIPMENT_PREPARING_SHIPMENT,
    SHIPMENT_SHIPPED,
    SHIPMENT_DELIVERED,
    SHIPMENT_CANCELLED,
    CREATING_SHIPMENT_LABEL_FAILED,
    SHIPMENT_DELIVERY_FAILED,
    // product
    PRODUCT_CREATED
}