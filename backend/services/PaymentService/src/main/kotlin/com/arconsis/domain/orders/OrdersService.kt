package com.arconsis.domain.orders

import com.arconsis.data.checkouts.CheckoutsRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.payments.PaymentsRepository
import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.domain.payments.toCreateOutboxEvent
import com.arconsis.domain.processedevents.ProcessedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    private val checkoutsRepository: CheckoutsRepository,
    private val paymentsRepository: PaymentsRepository,
    private val processedEventsRepository: ProcessedEventsRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
    private val objectMapper: ObjectMapper,
) {

    fun handleOrderEvents(eventId: UUID,  order: Order): Uni<Void> {
        return when (order.status) {
            OrderStatus.VALIDATED -> handleValidOrder(eventId, order)
            else -> Uni.createFrom().voidItem()
        }
    }

//    private fun handleValidOrder(eventId: UUID, order: Order): Uni<Void> {
//        return sessionFactory.withTransaction { session, _ ->
//            val proceedEvent = ProcessedEvent(
//                eventId = eventId,
//                processedAt = Instant.now()
//            )
//            processedEventsRepository.createEvent(proceedEvent, session)
//                .flatMap {
//                    paymentsRepository.createPayment(eventId, order.toCreatePayment(), session)
//                }
//                .flatMap { payment ->
//                    val createOutboxEvent = payment.toCreateOutboxEvent(objectMapper)
//                    outboxEventsRepository.createEvent(createOutboxEvent, session)
//                }
//                .map { null }
//        }
//    }

    private fun handleValidOrder(eventId: UUID, order: Order): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            val proceedEvent = ProcessedEvent(
                eventId = eventId,
                processedAt = Instant.now()
            )
            processedEventsRepository.createEvent(proceedEvent, session)
                .map {
                    checkoutsRepository.createCheckoutSession(order.orderId, order.items)
                }
                .flatMap { checkoutSession ->
                    paymentsRepository.createPayment(eventId, order.toCreatePayment(checkoutSession.id, checkoutSession.url), session)
                }
                .flatMap { payment ->
                    val createOutboxEvent = payment.toCreateOutboxEvent(objectMapper)
                    outboxEventsRepository.createEvent(createOutboxEvent, session)
                }
                .map { null }
        }
    }
}