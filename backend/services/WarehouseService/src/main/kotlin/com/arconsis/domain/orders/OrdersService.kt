package com.arconsis.domain.orders

import com.arconsis.data.common.toUni
import com.arconsis.data.inventory.InventoryRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.data.shipments.ShipmentsRepository
import com.arconsis.domain.ordersvalidations.OrderValidation
import com.arconsis.domain.ordersvalidations.OrderValidationStatus
import com.arconsis.domain.ordersvalidations.toCreateOutboxEvent
import com.arconsis.domain.processedevents.ProcessedEvent
import com.arconsis.domain.shipments.*
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import org.hibernate.reactive.mutiny.Mutiny.Session
import org.jboss.logging.Logger
import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class OrdersService(
	private val shipmentsRepository: ShipmentsRepository,
	private val inventoryRepository: InventoryRepository,
	private val outboxEventsRepository: OutboxEventsRepository,
	private val sessionFactory: Mutiny.SessionFactory,
	private val objectMapper: ObjectMapper,
	private val processedEventsRepository: ProcessedEventsRepository,
	private val logger: Logger
) {
	fun handleOrderEvents(eventId: UUID, order: Order): Uni<Void> {
		return when (order.status) {
			OrderStatus.REQUESTED -> handleOrderPending(eventId, order)
				.handleReduceStockError(eventId, order)
			OrderStatus.PAID -> handleOrderPaid(eventId, order)
			OrderStatus.PAYMENT_FAILED -> handleOrderPaymentFailed(eventId, order)
			else -> Uni.createFrom().voidItem()
		}
	}

	private fun handleOrderPending(eventId: UUID, order: Order): Uni<Void> {
		return sessionFactory.withTransaction { session, _ ->
			val proceedEvent = ProcessedEvent(
				eventId = eventId,
				processedAt = Instant.now()
			)

			processedEventsRepository.createEvent(proceedEvent, session)
				.flatMap {
					val unis: List<Uni<Boolean>> = order.items.map {
						inventoryRepository.reserveProductStock(it.productId, it.quantity, session)
					}
					Uni.combine().all().unis<List<Uni<Boolean>>>(unis).combinedWith {
						!it.contains(false)
					}
				}
				.createOrderValidation(order)
				.createOrderValidationEvent(session)
				.map { null }
		}
	}

	private fun handleOrderPaid(eventId: UUID, order: Order): Uni<Void> {
		return sessionFactory.withTransaction { session, _ ->
			val proceedEvent = ProcessedEvent(
				eventId = eventId,
				processedAt = Instant.now()
			)
			processedEventsRepository.createEvent(proceedEvent, session)
				.flatMap {
					val createShipment = CreateShipment(
						orderId = order.orderId,
						userId = order.userId,
						status = ShipmentStatus.PREPARING_SHIPMENT
					)
					shipmentsRepository.createShipment(createShipment, session)
				}
				.createShipmentEvent(session)
				.map { null }
		}
	}

	private fun handleOrderPaymentFailed(eventId: UUID, order: Order): Uni<Void> {
		return sessionFactory.withTransaction { session, _ ->
			val proceedEvent = ProcessedEvent(
				eventId = eventId,
				processedAt = Instant.now()
			)
			processedEventsRepository.createEvent(proceedEvent, session)
				.flatMap {
					val unis: List<Uni<Boolean>> = order.items.map {
						inventoryRepository.increaseProductStock(it.productId, it.quantity, session)
					}
					Uni.combine().all().unis<List<Uni<Boolean>>>(unis).combinedWith {
						!it.contains(false)
					}
				}
				.flatMap {
					Uni.createFrom().voidItem()
				}
		}
	}

	private fun Uni<Boolean>.createOrderValidation(order: Order) = map { stockUpdated ->
		val orderValidation = OrderValidation(
			orderId = order.orderId,
			userId = order.userId,
			status = if (stockUpdated) OrderValidationStatus.VALIDATED else OrderValidationStatus.INVALID,
			items = order.items
		)
		orderValidation
	}

	private fun Uni<OrderValidation>.createOrderValidationEvent(session: Session) = flatMap { orderValidation ->
		val createOutboxEvent = orderValidation.toCreateOutboxEvent(objectMapper)
		outboxEventsRepository.createEvent(createOutboxEvent, session)
	}

	private fun Uni<Shipment>.createShipmentEvent(session: Session) = flatMap { shipment ->
		val createOutboxEvent = shipment.toCreateOutboxEvent(objectMapper)
		outboxEventsRepository.createEvent(createOutboxEvent, session)
	}

	private fun Uni<Void>.handleReduceStockError(eventId: UUID, order: Order) = onFailure()
		.recoverWithUni { err ->
			logger.error("handleReduceStockError failed with error: ${err.localizedMessage}")
			if (err.localizedMessage?.contains(INVENTORY_STOCK_ERROR) == true) {
				val proceedEvent = ProcessedEvent(
					eventId = eventId,
					processedAt = Instant.now()
				)
				sessionFactory.withTransaction { session, _ ->
					processedEventsRepository.createEvent(proceedEvent, session)
						.flatMap {
							false.toUni()
						}
						.createOrderValidation(order)
						.createOrderValidationEvent(session)
						.map {
							null
						}
				}
			} else {
				null
			}
		}

	companion object {
		const val INVENTORY_STOCK_ERROR =
			"new row for relation \"inventory\" violates check constraint \"inventory_stock_check\""
	}
}