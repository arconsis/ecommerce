package com.arconsis.domain.shipments

import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.toCreateOutboxEvent
import com.arconsis.domain.processedevents.ProcessedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.smallrye.mutiny.Uni
import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShipmentsService(
	private val ordersRepository: OrdersRepository,
	private val outboxEventsRepository: OutboxEventsRepository,
	private val processedEventsRepository: ProcessedEventsRepository,
	private val objectMapper: ObjectMapper,
) {
	@ReactiveTransactional
	fun handleShipmentEvents(eventId: UUID, shipment: Shipment): Uni<Void> {
		return when (shipment.status) {
			ShipmentStatus.DELIVERED -> handleDeliveredShipment(eventId, shipment)
			ShipmentStatus.SHIPPED -> handleOutForShipment(eventId, shipment)
			else -> return Uni.createFrom().voidItem()
		}
	}

	@ReactiveTransactional
	private fun handleDeliveredShipment(eventId: UUID, shipment: Shipment): Uni<Void> {
		val proceedEvent = ProcessedEvent(
			eventId = eventId,
			processedAt = Instant.now()
		)
		val orderStatus = OrderStatus.COMPLETED
		return processedEventsRepository.createEvent(proceedEvent)
			.updateOrderStatus(shipment, orderStatus)
			.createOutboxEvent()
			.map {
				null
			}
	}

	@ReactiveTransactional
	private fun handleOutForShipment(eventId: UUID, shipment: Shipment): Uni<Void> {
		val proceedEvent = ProcessedEvent(
			eventId = eventId,
			processedAt = Instant.now()
		)
		val orderStatus = OrderStatus.SHIPPED
		return processedEventsRepository.createEvent(proceedEvent)
			.updateOrderStatus(shipment, orderStatus)
			.createOutboxEvent()
			.map {
				null
			}
	}

	private fun Uni<ProcessedEvent>.updateOrderStatus(
		shipment: Shipment,
		orderStatus: OrderStatus,
	) = flatMap {
		ordersRepository.updateOrderStatus(shipment.orderId, orderStatus)
	}

	private fun Uni<Order>.createOutboxEvent() = flatMap { order ->
		val createOutboxEvent = order.toCreateOutboxEvent(objectMapper)
		outboxEventsRepository.createEvent(createOutboxEvent)
	}
}