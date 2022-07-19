package com.arconsis.data.processedevents

import com.arconsis.domain.processedevents.ProcessedEvent
import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProceedEventsDataStore : PanacheRepository<ProcessedEventEntity> {
	fun createEvent(event: ProcessedEvent): Uni<ProcessedEvent> {
		val eventEntity = event.toProcessedEventEntity()
		return persist(eventEntity)
			.map {
				it.toProcessedEvent()
			}
	}

	fun getEvent(eventId: UUID): Uni<ProcessedEvent?> {
		return find("eventId", eventId)
			.firstResult<ProcessedEventEntity?>()
			.map {
				it.toProcessedEvent()
			}
	}
}