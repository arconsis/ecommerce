package com.arconsis.domain.ordersvalidations

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toCreateOutboxEvent
import com.arconsis.domain.processedevents.ProcessedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.smallrye.mutiny.Uni
import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderValidationsService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val processedEventsRepository: ProcessedEventsRepository,
    private val objectMapper: ObjectMapper
) {

    @ReactiveTransactional
    fun handleOrderValidationEvents(eventId: UUID, orderValidation: OrderValidation): Uni<Void> {
        return when (orderValidation.status) {
            OrderValidationStatus.VALIDATED -> handleValidOrderValidation(eventId, orderValidation)
            OrderValidationStatus.INVALID -> handleValidOrderInvalidation(eventId, orderValidation)
        }
    }

    @ReactiveTransactional
    private fun handleValidOrderValidation(eventId: UUID, orderValidation: OrderValidation): Uni<Void> {
        val proceedEvent = ProcessedEvent(
            eventId = eventId,
            processedAt = Instant.now()
        )
        val orderStatus = OrderStatus.VALIDATED
        return processedEventsRepository.createEvent(proceedEvent)
            .updateOrderStatus(orderValidation, orderStatus)
            .createOutboxEvent()
            .map {
                null
            }
    }

    @ReactiveTransactional
    private fun handleValidOrderInvalidation(eventId: UUID, orderValidation: OrderValidation): Uni<Void> {
        val proceedEvent = ProcessedEvent(
            eventId = eventId,
            processedAt = Instant.now()
        )
        val orderStatus = OrderStatus.OUT_OF_STOCK
        return processedEventsRepository.createEvent(proceedEvent)
            .updateOrderStatus(orderValidation, orderStatus)
            .map {
                null
            }
    }

    private fun Uni<ProcessedEvent>.updateOrderStatus(
        orderValidation: OrderValidation,
        orderStatus: OrderStatus,
    ) = flatMap { event ->
        if (event != null) Uni.createFrom().voidItem()
        ordersRepository.updateOrderStatus(orderValidation.orderId, orderStatus)
    }

    private fun Uni<Order>.createOutboxEvent() = flatMap { order ->
        val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent)
    }
}