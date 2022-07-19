package com.arconsis.presentation.events.orders

import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrdersService
import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.jboss.logging.Logger
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrderEventsResource(
    private val ordersService: OrdersService,
    private val objectMapper: ObjectMapper,
    private val logger: Logger
) {
    @Incoming("orders-in")
    fun consumeOrderEvents(orderRecord: Record<String, OrderEventDto>): Uni<Void> {
        val orderEventDto = orderRecord.value()
        val eventId = UUID.fromString(orderEventDto.payload.currentValue.id)
        val order = objectMapper.readValue(orderEventDto.payload.currentValue.toOutboxEvent().payload, Order::class.java)
        return ordersService.handleOrderEvents(eventId, order)
            .onFailure {
                logger.error("Error on consumeOrderEvents", it)
                false
            }
            .recoverWithNull()
    }
}