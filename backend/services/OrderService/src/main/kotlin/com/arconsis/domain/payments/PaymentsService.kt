package com.arconsis.domain.payments

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
class PaymentsService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val processedEventsRepository: ProcessedEventsRepository,
    private val objectMapper: ObjectMapper,
) {
    @ReactiveTransactional
    fun handlePaymentEvents(eventId: UUID, payment: Payment): Uni<Void> {
        return when (payment.status) {
            PaymentStatus.SUCCEED -> handleSucceedPayment(eventId, payment).replaceWithVoid()
            PaymentStatus.FAILED -> handleFailedPayment(eventId, payment).replaceWithVoid()
        }
    }

    private fun handleSucceedPayment(eventId: UUID, payment: Payment): Uni<Void> {
        val proceedEvent = ProcessedEvent(
            eventId = eventId,
            processedAt = Instant.now()
        )
        val orderStatus = OrderStatus.PAID
        return processedEventsRepository.createEvent(proceedEvent)
            .updateOrderStatus(payment, orderStatus)
            .createOutboxEvent()
            .map {
                null
            }
    }

    private fun handleFailedPayment(eventId: UUID, payment: Payment): Uni<Void> {
        val proceedEvent = ProcessedEvent(
            eventId = eventId,
            processedAt = Instant.now()
        )
        val orderStatus = OrderStatus.PAYMENT_FAILED
        return processedEventsRepository.createEvent(proceedEvent)
            .updateOrderStatus(payment, orderStatus)
            .createOutboxEvent()
            .map {
                null
            }
    }

    private fun Uni<ProcessedEvent>.updateOrderStatus(
        payment: Payment,
        orderStatus: OrderStatus,
    ) = flatMap { _ ->
        ordersRepository.updateOrderStatus(payment.orderId, orderStatus)
    }

    private fun Uni<Order>.createOutboxEvent() = flatMap { order ->
        val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent)
    }
}