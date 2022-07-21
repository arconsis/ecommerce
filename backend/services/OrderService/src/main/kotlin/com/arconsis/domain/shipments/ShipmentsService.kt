package com.arconsis.domain.shipments

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
class ShipmentsService(
	private val basketsRepository: BasketsRepository,
	private val ordersRepository: OrdersRepository,
	private val outboxEventsRepository: OutboxEventsRepository,
	private val processedEventsRepository: ProcessedEventsRepository,
	private val sessionFactory: Mutiny.SessionFactory,
	private val objectMapper: ObjectMapper,
) {
	fun handleShipmentEvents(eventId: UUID, shipment: Shipment): Uni<Void> {
		return when (shipment.status) {
			ShipmentStatus.DELIVERED -> handleDeliveredShipment(eventId, shipment)
			ShipmentStatus.SHIPPED -> handleOutForShipment(eventId, shipment)
			else -> return Uni.createFrom().voidItem()
		}
	}

	private fun handleDeliveredShipment(eventId: UUID, shipment: Shipment): Uni<Void> {
		return sessionFactory.withTransaction { session, _ ->
			val proceedEvent = ProcessedEvent(
				eventId = eventId,
				processedAt = Instant.now()
			)
			val orderStatus = OrderStatus.COMPLETED
			processedEventsRepository.createEvent(proceedEvent, session)
				.updateOrderStatus(shipment, orderStatus, session)
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

	private fun handleOutForShipment(eventId: UUID, shipment: Shipment): Uni<Void> {
		return sessionFactory.withTransaction { session, _ ->
			val proceedEvent = ProcessedEvent(
				eventId = eventId,
				processedAt = Instant.now()
			)
			val orderStatus = OrderStatus.SHIPPED
			processedEventsRepository.createEvent(proceedEvent, session)
				.updateOrderStatus(shipment, orderStatus, session)
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
		shipment: Shipment,
		orderStatus: OrderStatus,
		session: Session
	) = flatMap {
		ordersRepository.updateOrderStatus(shipment.orderId, orderStatus, session)
	}

	private fun Uni<Order>.createOutboxEvent(session: Session) = flatMap { order ->
		val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
		outboxEventsRepository.createEvent(createOutboxEvent, session)
	}
}