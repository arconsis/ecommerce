package com.arconsis.domain.payments

import com.arconsis.common.asPair
import com.arconsis.common.toUni
import com.arconsis.data.baskets.BasketsRepository
import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.domain.baskets.toOrderItem
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
    private val basketsRepository: BasketsRepository,
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val processedEventsRepository: ProcessedEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
    private val objectMapper: ObjectMapper,
) {
    fun handlePaymentEvents(eventId: UUID, payment: Payment): Uni<Void> {
        return when (payment.status) {
            PaymentStatus.IN_PROGRESS -> handlePaymentInProgress(eventId, payment)
            PaymentStatus.SUCCEED -> handleSucceedPayment(eventId, payment)
            PaymentStatus.FAILED -> handleFailedPayment(eventId, payment)
        }
    }

    // Let's assume that client makes polling to GET /orders/:orderId to fetch order status -> here we set it to PAYMENT_IN_PROGRESS and client can fetch checkout url
    private fun handlePaymentInProgress(eventId: UUID, payment: Payment): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            val proceedEvent = ProcessedEvent(
                eventId = eventId,
                processedAt = Instant.now()
            )
            val orderStatus = OrderStatus.PAYMENT_IN_PROGRESS
            processedEventsRepository.createEvent(proceedEvent, session)
                .updateOrderCheckout(payment, orderStatus, session)
                .flatMap { order ->
                    Uni.combine().all().unis(
                        basketsRepository.getBasket(order.basketId, session),
                        order.toUni(),
                    ).asPair()
                }
                .map { (basket, order) ->
                    val enrichedOrder = order.copy(items = basket!!.items.map { it.toOrderItem(order.orderId) })
                    enrichedOrder
                }
                .createOutboxEvent(session)
                .map {
                    null
                }
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
                .flatMap { order ->
                    Uni.combine().all().unis(
                        basketsRepository.getBasket(order.basketId, session),
                        order.toUni(),
                    ).asPair()
                }
                .map { (basket, order) ->
                    val enrichedOrder = order.copy(items = basket!!.items.map { it.toOrderItem(order.orderId) })
                    enrichedOrder
                }
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
                .flatMap { order ->
                    Uni.combine().all().unis(
                        basketsRepository.getBasket(order.basketId, session),
                        order.toUni(),
                    ).asPair()
                }
                .map { (basket, order) ->
                    val enrichedOrder = order.copy(items = basket!!.items.map { it.toOrderItem(order.orderId) })
                    enrichedOrder
                }
                .createOutboxEvent(session)
                .map {
                    null
                }
        }
    }

    private fun Uni<ProcessedEvent>.updateOrderCheckout(
        payment: Payment,
        orderStatus: OrderStatus,
        session: Session
    ) = flatMap {
        ordersRepository.updateOrderCheckout(payment.orderId, orderStatus, payment.checkout.checkoutSessionId, payment.checkout.checkoutUrl, session)
    }

    private fun Uni<ProcessedEvent>.updateOrderStatus(
        payment: Payment,
        orderStatus: OrderStatus,
        session: Session
    ) = flatMap {
        ordersRepository.updateOrderStatus(payment.orderId, orderStatus, session)
    }

    private fun Uni<Order>.createOutboxEvent(session: Session) = flatMap { order ->
        val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent, session)
    }
}