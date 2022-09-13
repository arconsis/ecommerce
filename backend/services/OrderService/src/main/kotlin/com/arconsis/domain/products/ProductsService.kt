package com.arconsis.domain.products

import com.arconsis.data.processedevents.ProcessedEventsRepository
import com.arconsis.data.products.ProductsRepository
import com.arconsis.domain.outboxevents.OutboxEventType
import com.arconsis.domain.processedevents.ProcessedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.time.Instant
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProductsService(
	private val productsRepository: ProductsRepository,
	private val processedEventsRepository: ProcessedEventsRepository,
	private val sessionFactory: Mutiny.SessionFactory,
	private val objectMapper: ObjectMapper,
) {

	fun handleProductEvents(eventId: UUID, product: Product, type: OutboxEventType): Uni<Void> {
		return when (type) {
			OutboxEventType.PRODUCT_CREATED -> proceedToCreateProduct(eventId, product)
			else -> return Uni.createFrom().voidItem()
		}
	}

	private fun proceedToCreateProduct(eventId: UUID, product: Product): Uni<Void> {
		return sessionFactory.withTransaction { session, _ ->
			val proceedEvent = ProcessedEvent(
				eventId = eventId,
				processedAt = Instant.now()
			)
			processedEventsRepository.createEvent(proceedEvent, session)
				.flatMap {
					productsRepository.createProduct(product, session)
				}
				.map {
					null
				}
		}
	}
}