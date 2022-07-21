package com.arconsis.domain.ordersvalidations

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
import org.jboss.logging.Logger
import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderValidationsService(
    private val basketsRepository: BasketsRepository,
    private val ordersRepository: OrdersRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val processedEventsRepository: ProcessedEventsRepository,
    private val sessionFactory: Mutiny.SessionFactory,
    private val objectMapper: ObjectMapper,
    private val logger: Logger
) {

    fun handleOrderValidationEvents(eventId: UUID, orderValidation: OrderValidation): Uni<Void> {
        return when (orderValidation.status) {
            OrderValidationStatus.VALIDATED -> handleValidOrderValidation(eventId, orderValidation)
                .onFailure()
                .recoverWithUni { err ->
                    logger.error("handleReduceStockError failed with error: ${err.localizedMessage}")
                    null
                }
            OrderValidationStatus.INVALID -> handleValidOrderInvalidation(eventId, orderValidation)
        }
    }

    private fun handleValidOrderValidation(eventId: UUID, orderValidation: OrderValidation): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            val proceedEvent = ProcessedEvent(
                eventId = eventId,
                processedAt = Instant.now()
            )
            val orderStatus = OrderStatus.VALIDATED
            processedEventsRepository.createEvent(proceedEvent, session)
                .updateOrderStatus(orderValidation, orderStatus, session)
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

    private fun handleValidOrderInvalidation(eventId: UUID, orderValidation: OrderValidation): Uni<Void> {
        return sessionFactory.withTransaction { session, _ ->
            val proceedEvent = ProcessedEvent(
                eventId = eventId,
                processedAt = Instant.now()
            )
            val orderStatus = OrderStatus.OUT_OF_STOCK
            processedEventsRepository.createEvent(proceedEvent, session)
                .updateOrderStatus(orderValidation, orderStatus, session)
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
                .map {
                    null
                }
        }
    }

    private fun Uni<ProcessedEvent>.updateOrderStatus(
        orderValidation: OrderValidation,
        orderStatus: OrderStatus,
        session: Session
    ) = flatMap {
        ordersRepository.updateOrderStatus(orderValidation.orderId, orderStatus, session)
    }

    private fun Uni<Order>.createOutboxEvent(session: Session) = flatMap { order ->
        val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent, session)
    }
}