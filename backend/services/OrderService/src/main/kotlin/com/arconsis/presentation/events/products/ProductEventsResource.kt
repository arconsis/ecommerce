package com.arconsis.presentation.events.products

import com.arconsis.domain.outboxevents.AggregateType
import com.arconsis.domain.products.ProductsService
import com.arconsis.presentation.events.common.WarehouseEventDto
import com.arconsis.presentation.events.common.toOutboxEvent
import com.arconsis.presentation.events.products.dto.request.ProductEventDto
import com.arconsis.presentation.events.products.dto.request.toProduct
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.jboss.logging.Logger
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProductEventsResource(
    private val productsService: ProductsService,
    private val objectMapper: ObjectMapper,
    private val logger: Logger
) {
    @Incoming("warehouse-in")
    fun consumeWarehouseEvents(warehouseEventsDto: Record<String, WarehouseEventDto>): Uni<Void> {
        val productEventDto = warehouseEventsDto.value()
        val outboxEvent = productEventDto.payload.currentValue.toOutboxEvent()
        if (outboxEvent.aggregateType != AggregateType.PRODUCT) {
            return Uni.createFrom().voidItem()
        }
        val eventId = UUID.fromString(productEventDto.payload.currentValue.id)
        val product = objectMapper.readValue(
            outboxEvent.payload,
            ProductEventDto::class.java
        ).toProduct()
        return productsService.handleProductEvents(eventId, product, outboxEvent.type)
            .onFailure {
                logger.error("Handling products events failed because of: ", it)
                false
            }
            .recoverWithNull()
    }
}
