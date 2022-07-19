package com.arconsis.domain.payments

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toCreateOutboxEvent
import com.arconsis.domain.processedevents.ProcessedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import org.hibernate.reactive.mutiny.Mutiny.Session
import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsService(
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val processedEventsRepository: ProcessedEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
    private val objectMapper: ObjectMapper,
) {
    fun handlePaymentEvents(eventId: UUID, payment: Payment): Uni<Void> {
        return when (payment.status) {
            PaymentStatus.SUCCEED -> handleSucceedPayment(eventId, payment)
            PaymentStatus.FAILED -> handleFailedPayment(eventId, payment)
        }
    }

    private fun handleSucceedPayment(eventId: UUID, payment: Payment): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            val proceedEvent = ProcessedEvent(
                eventId = eventId,
                processedAt = Instant.now()
            )
            val orderStatus = OrderStatus.PAID
            processedEventsRepository.createEvent(proceedEvent, session)
                .updateOrderStatus(payment, orderStatus, session)
                .createOutboxEvent(session)
                .map {
                    null
                }
        }

    }

    private fun handleFailedPayment(eventId: UUID, payment: Payment): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            val proceedEvent = ProcessedEvent(
                eventId = eventId,
                processedAt = Instant.now()
            )
            val orderStatus = OrderStatus.PAYMENT_FAILED
            processedEventsRepository.createEvent(proceedEvent, session)
                .updateOrderStatus(payment, orderStatus, session)
                .createOutboxEvent(session)
                .map {
                    null
                }
        }
    }

    private fun Uni<ProcessedEvent>.updateOrderStatus(
        payment: Payment,
        orderStatus: OrderStatus,
        session: Session
    ) = flatMap { _ ->
        ordersRepository.updateOrderStatus(payment.orderId, orderStatus, session)
    }

    private fun Uni<Order>.createOutboxEvent(session: Session) = flatMap { order ->
        val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent, session)
    }
}