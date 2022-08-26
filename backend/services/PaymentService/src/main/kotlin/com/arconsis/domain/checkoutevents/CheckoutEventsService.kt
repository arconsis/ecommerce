package com.arconsis.domain.checkoutevents

import com.arconsis.common.asPair
import com.arconsis.common.errors.abort
import com.arconsis.common.toUni
import com.arconsis.data.checkoutevents.CheckoutEventsRepository
import com.arconsis.domain.checkouts.CheckoutsFailureReason
import com.arconsis.data.checkouts.CheckoutsRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.domain.checkouts.CheckoutStatus
import com.arconsis.domain.checkouts.toCreateOutboxEvent
import com.arconsis.presentation.http.payments.dto.CreateCheckoutEventDto
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import org.hibernate.reactive.mutiny.Mutiny.Session
import org.jboss.logging.Logger
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CheckoutEventsService(
	private val checkoutEventsRepository: CheckoutEventsRepository,
	private val checkoutRepository: CheckoutsRepository,
	private val outboxEventsRepository: OutboxEventsRepository,
	private val sessionFactory: Mutiny.SessionFactory,
	private val objectMapper: ObjectMapper,
	private val logger: Logger
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
					logger.info("Webhook checkout event for checkout with id: ${checkoutEvent.checkoutId} with type ${checkoutEvent.type} just consumed")
					when (checkoutEvent.type) {
						CHARGE_SUCCESS -> setPaymentAsSucceed(checkoutEvent, session)
						CHARGE_FAILED -> setPaymentAsFailed(
							checkoutEvent,
							checkoutEventDto.paymentErrorMessage,
							checkoutEventDto.paymentErrorCode,
							session
						)
						else -> {
							checkoutEvent.toUni()
						}
					}
				}
				.map {
					it
				}
		}
	}

	private fun setPaymentAsSucceed(checkoutEvent: CheckoutEvent, session: Session): Uni<CheckoutEvent> {
		logger.info("Proceed to set payment as succeed for checkout with id: ${checkoutEvent.checkoutId} because of Webhook checkout event with type ${checkoutEvent.type} ")
		return checkoutRepository.updateCheckout(checkoutEvent.checkoutId, CheckoutStatus.PAYMENT_SUCCEED, session)
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
	}


	private fun setPaymentAsFailed(
		checkoutEvent: CheckoutEvent,
		paymentErrorMessage: String?,
		paymentErrorCode: String?,
		session: Session
	): Uni<CheckoutEvent> {
		logger.error("Proceed to set payment as failed for checkout with id: ${checkoutEvent.checkoutId} because of Webhook checkout event with type ${checkoutEvent.type} ")
		return checkoutRepository.updateCheckout(
			checkoutId = checkoutEvent.checkoutId,
			paymentStatus = CheckoutStatus.PAYMENT_FAILED,
			paymentErrorMessage = paymentErrorMessage,
			paymentErrorCode = paymentErrorCode,
			session = session
		)
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
	}


	companion object {
		private const val CHARGE_SUCCESS = "charge.succeeded"
		private const val CHARGE_FAILED = "charge.failed"
	}
}