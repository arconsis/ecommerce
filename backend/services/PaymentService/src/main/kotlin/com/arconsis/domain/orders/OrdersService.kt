package com.arconsis.domain.orders

import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.payments.PaymentsRepository
import com.arconsis.data.payments.toCreatePayment
import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.domain.payments.toCreateOutboxEvent
import com.arconsis.domain.processedevents.ProcessedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.smallrye.mutiny.Uni
import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    private val paymentsRepository: PaymentsRepository,
    private val processedEventsRepository: ProcessedEventsRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper,
) {

    @ReactiveTransactional
    fun handleOrderEvents(eventId: UUID,  order: Order): Uni<Void> {
        return when (order.status) {
            OrderStatus.VALIDATED -> handleValidOrder(eventId, order)
            else -> Uni.createFrom().voidItem()
        }
    }

    private fun handleValidOrder(eventId: UUID, order: Order): Uni<Void> {
        val proceedEvent = ProcessedEvent(
            eventId = eventId,
            processedAt = Instant.now()
        )
        return processedEventsRepository.createEvent(proceedEvent)
            .flatMap {
                paymentsRepository.createPayment(eventId, order.toCreatePayment())
            }
            .flatMap { payment ->
                val createOutboxEvent = payment.toCreateOutboxEvent(objectMapper)
                outboxEventsRepository.createEvent(createOutboxEvent)
            }
            .map { null }
    }
}