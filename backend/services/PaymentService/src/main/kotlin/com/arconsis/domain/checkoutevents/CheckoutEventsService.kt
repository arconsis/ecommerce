package com.arconsis.domain.checkoutevents

import com.arconsis.common.asPair
import com.arconsis.common.errors.abort
import com.arconsis.common.toUni
import com.arconsis.data.checkoutevents.CheckoutEventsRepository
import com.arconsis.data.checkouts.CheckoutsFailureReason
import com.arconsis.data.checkouts.CheckoutsRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.domain.checkouts.CheckoutStatus
import com.arconsis.domain.checkouts.toCreateOutboxEvent
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
						abort(CheckoutsFailureReason.CheckoutNotFound)
					} else {
						CreateCheckoutEvent(
							type = checkoutEventDto.type,
							pspData = checkoutEventDto.pspData,
							pspReferenceId = checkoutEventDto.pspReferenceId,
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