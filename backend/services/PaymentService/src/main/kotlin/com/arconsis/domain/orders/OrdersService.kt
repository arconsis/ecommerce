package com.arconsis.domain.orders

import com.arconsis.data.checkouts.CheckoutsRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.payments.PaymentsRepository
import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.domain.checkouts.CheckoutStatus
import com.arconsis.domain.checkouts.toCreateOutboxEvent
import com.arconsis.domain.processedevents.ProcessedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import org.jboss.logging.Logger
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
	private val logger: Logger
) {

	fun handleOrderEvents(eventId: UUID, order: Order): Uni<Void> {
		return when (order.status) {
			OrderStatus.VALIDATED -> startCheckout(eventId, order).flatMap {
				paymentsRepository.charge(order)
			}.map { null }
			else -> Uni.createFrom().voidItem()
		}
	}

	private fun startCheckout(eventId: UUID, order: Order): Uni<Void> =
		sessionFactory.withTransaction { session, _ ->
			val proceedEvent = ProcessedEvent(
				eventId = eventId,
				processedAt = Instant.now()
			)
			processedEventsRepository.createEvent(proceedEvent, session)
				.flatMap {
					checkoutsRepository.createCheckout(order, CheckoutStatus.PAYMENT_IN_PROGRESS, session)
				}
				.flatMap { checkout ->
					val createOutboxEvent = checkout.toCreateOutboxEvent(objectMapper)
					outboxEventsRepository.createEvent(createOutboxEvent, session)
				}
				.map { null }
		}
}