package com.arconsis.domain.orders

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
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.smallrye.mutiny.Uni
import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    private val shipmentsRepository: ShipmentsRepository,
    private val inventoryRepository: InventoryRepository,
    private val outboxEventsRepository: OutboxEventsRepository,
    private val objectMapper: ObjectMapper,
    private val processedEventsRepository: ProcessedEventsRepository
) {
    @ReactiveTransactional
    fun handleOrderEvents(eventId: UUID, order: Order): Uni<Void> {
        return when (order.status) {
            OrderStatus.REQUESTED -> handleOrderPending(eventId, order)
            OrderStatus.PAID -> handleOrderPaid(eventId, order)
            OrderStatus.PAYMENT_FAILED -> handleOrderPaymentFailed(eventId, order)
            else -> Uni.createFrom().voidItem()
        }
    }

    @ReactiveTransactional
    private fun handleOrderPending(eventId: UUID, order: Order): Uni<Void> {
        val proceedEvent = ProcessedEvent(
            eventId = eventId,
            processedAt = Instant.now()
        )
        return processedEventsRepository.createEvent(proceedEvent)
            .flatMap { event ->
                inventoryRepository.reserveProductStock(order.productId, order.quantity)
            }
            .createOrderValidation(order)
            .createOrderValidationEvent()
            .map {
                null
            }
    }

    @ReactiveTransactional
    private fun handleOrderPaid(eventId: UUID, order: Order): Uni<Void> {
        val proceedEvent = ProcessedEvent(
            eventId = eventId,
            processedAt = Instant.now()
        )
        return processedEventsRepository.createEvent(proceedEvent)
            .flatMap { event ->
                val createShipment = CreateShipment(
                    orderId = order.id,
                    userId = order.userId,
                    status = ShipmentStatus.PREPARING_SHIPMENT
                )
                shipmentsRepository.createShipment(createShipment)
            }
            .updateShipmentStatus(ShipmentStatus.SHIPPED)
            .createShipmentEvent()
            .map {
                null
            }
    }

    @ReactiveTransactional
    private fun handleOrderPaymentFailed(eventId: UUID, order: Order): Uni<Void> {
        val proceedEvent = ProcessedEvent(
            eventId = eventId,
            processedAt = Instant.now()
        )
        return processedEventsRepository.createEvent(proceedEvent)
            .flatMap {
                inventoryRepository.increaseProductStock(order.productId, order.quantity)
            }
            .flatMap {
                Uni.createFrom().voidItem()
            }
    }

    private fun Uni<Boolean>.createOrderValidation(order: Order) = map { stockUpdated ->
        val orderValidation = OrderValidation(
            productId = order.productId,
            quantity = order.quantity,
            orderId = order.id,
            userId = order.userId,
            status = if (stockUpdated) OrderValidationStatus.VALIDATED else OrderValidationStatus.INVALID
        )
        orderValidation
    }

    private fun Uni<OrderValidation>.createOrderValidationEvent() = flatMap { orderValidation ->
        val createOutboxEvent = orderValidation.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent)
    }

    private fun Uni<Shipment>.updateShipmentStatus(status: ShipmentStatus) = flatMap { shipment ->
        val updateShipment = UpdateShipment(
            shipment.id,
            status
        )
        shipmentsRepository.updateShipmentStatus(updateShipment)
    }

    private fun Uni<Shipment>.createShipmentEvent() = flatMap { shipment ->
        val createOutboxEvent = shipment.toCreateOutboxEvent(objectMapper)
        outboxEventsRepository.createEvent(createOutboxEvent)
    }
}