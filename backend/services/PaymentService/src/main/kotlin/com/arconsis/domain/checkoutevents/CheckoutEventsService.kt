package com.arconsis.domain.checkoutevents

import com.arconsis.common.asPair
import com.arconsis.common.toUni
import com.arconsis.data.checkoutevents.CheckoutEventsRepository
import com.arconsis.data.checkouts.CheckoutsRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.payments.PaymentsRepository
import com.arconsis.domain.checkouts.CheckoutStatus
import com.arconsis.domain.checkouts.toCreateOutboxEvent
import com.arconsis.domain.payments.CreatePayment
import com.arconsis.presentation.http.payments.dto.CreateCheckoutEventDto
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CheckoutEventsService(
	private val checkoutEventsRepository: CheckoutEventsRepository,
	private val checkoutRepository: CheckoutsRepository,
	private val outboxEventsRepository: OutboxEventsRepository,
	private val paymentsRepository: PaymentsRepository,
	private val sessionFactory: Mutiny.SessionFactory,
	private val objectMapper: ObjectMapper,
) {
	fun createCheckoutEvent(checkoutEventDto: CreateCheckoutEventDto): Uni<CheckoutEvent?> {
		return sessionFactory.withTransaction { session, _ ->
			checkoutRepository.getCheckoutByOrderId(
				UUID.fromString(checkoutEventDto.orderId),
				session
			)
				.map { checkout ->
					if (checkout == null) {
						throw RuntimeException("No checkout found")
					} else {
						CreateCheckoutEvent(
							type = checkoutEventDto.type,
							metadata = checkoutEventDto.metadata,
							checkoutId = checkout.checkoutId,
						)
					}
				}
				.flatMap { event ->
					checkoutEventsRepository.createCheckoutEvent(event, session)
				}
				.flatMap { checkoutEvent ->
					if (checkoutEvent.type == CHARGE_SUCCESS) {
						checkoutRepository.updateCheckoutStatus(checkoutEvent.checkoutId, CheckoutStatus.PAYMENT_SUCCEED, session)
							.flatMap { checkout ->
								val createOutboxEvent = checkout.toCreateOutboxEvent(objectMapper)
								Uni.combine().all().unis(
									outboxEventsRepository.createEvent(createOutboxEvent, session),
									checkout.toUni(),
								).asPair()
							}
							.flatMap { (_, checkout) ->
								// TODO: find psp reference from stripe
								paymentsRepository.createPayment(CreatePayment(checkoutId = checkout.checkoutId, UUID.randomUUID().toString()), session)
							}
							.map {
								checkoutEvent
							}
					} else {
						checkoutEvent.toUni()
					}
				}
		}
	}

	companion object {
		private const val CHARGE_SUCCESS = "charge.succeeded"
	}
}