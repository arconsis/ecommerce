package com.arconsis.domain.checkout

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
class CheckoutsService(
    private val basketsRepository: BasketsRepository,
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val processedEventsRepository: ProcessedEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
    private val objectMapper: ObjectMapper,
) {
    fun handlePaymentEvents(eventId: UUID, checkout: Checkout): Uni<Void> {
        return when (checkout.paymentStatus) {
            CheckoutStatus.PAYMENT_IN_PROGRESS -> handlePaymentInProgress(eventId, checkout)
            CheckoutStatus.PAYMENT_SUCCEED -> handleSucceedPayment(eventId, checkout)
            CheckoutStatus.PAYMENT_FAILED -> handleFailedPayment(eventId, checkout)
        }
    }

    // Let's assume that client makes polling to GET /orders/:orderId to fetch order status -> here we set it to PAYMENT_IN_PROGRESS and client can fetch checkout url
    private fun handlePaymentInProgress(eventId: UUID, checkout: Checkout): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            val proceedEvent = ProcessedEvent(
                eventId = eventId,
                processedAt = Instant.now()
            )
            val orderStatus = OrderStatus.PAYMENT_IN_PROGRESS
            processedEventsRepository.createEvent(proceedEvent, session)
                .updateOrderStatus(checkout, orderStatus, session)
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

    private fun handleSucceedPayment(eventId: UUID, checkout: Checkout): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            val proceedEvent = ProcessedEvent(
                eventId = eventId,
                processedAt = Instant.now()
            )
            val orderStatus = OrderStatus.PAID
            processedEventsRepository.createEvent(proceedEvent, session)
                .updateOrderStatus(checkout, orderStatus, session)
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

    private fun handleFailedPayment(eventId: UUID, checkout: Checkout): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            val proceedEvent = ProcessedEvent(
                eventId = eventId,
                processedAt = Instant.now()
            )
            val orderStatus = OrderStatus.PAYMENT_FAILED
            processedEventsRepository.createEvent(proceedEvent, session)
                .updateOrderStatus(checkout, orderStatus, session)
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

    private fun Uni<ProcessedEvent>.updateOrderStatus(
        checkout: Checkout,
        orderStatus: OrderStatus,
        session: Session
    ) = flatMap {
        ordersRepository.updateOrderStatus(checkout.orderId, orderStatus, session)
    }

    private fun Uni<Order>.createOutboxEvent(session: Session) = flatMap { order ->
        val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent, session)
    }
}