package com.arconsis.domain.checkoutevents

import com.arconsis.common.toUni
import com.arconsis.data.checkoutevents.CheckoutEventsRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.payments.PaymentsRepository
import com.arconsis.domain.payments.PaymentStatus
import com.arconsis.domain.payments.toCreateOutboxEvent
import com.arconsis.presentation.http.payments.dto.CreateCheckoutEventDto
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CheckoutEventsService(
	private val checkoutEventsRepository: CheckoutEventsRepository,
	private val outboxEventsRepository: OutboxEventsRepository,
	private val paymentsRepository: PaymentsRepository,
	private val sessionFactory: Mutiny.SessionFactory,
	private val objectMapper: ObjectMapper,
) {
	fun createCheckoutEvent(checkoutEventDto: CreateCheckoutEventDto): Uni<CheckoutEvent?> {
		return sessionFactory.withTransaction { session, _ ->
			paymentsRepository.getPaymentByOrderId(
				UUID.fromString(checkoutEventDto.orderId),
				session
			)
				.map { payment ->
					if (payment == null) {
						throw RuntimeException("No payment found")
					} else {
						CreateCheckoutEvent(
							type = checkoutEventDto.type,
							metadata = checkoutEventDto.metadata,
							paymentId = payment.paymentId!!,
						)
					}
				}
				.flatMap { event ->
					checkoutEventsRepository.createCheckoutEvent(event, session)
				}
				.flatMap { checkoutEvent ->
					if (checkoutEvent.type == CHARGE_SUCCESS) {
						paymentsRepository.updatePaymentStatus(checkoutEvent.paymentId, PaymentStatus.SUCCEED, session)
							.flatMap { payment ->
								val createOutboxEvent = payment.toCreateOutboxEvent(objectMapper)
								outboxEventsRepository.createEvent(createOutboxEvent, session)
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