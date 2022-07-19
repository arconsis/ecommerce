package com.arconsis.data.outboxevents

import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.outboxevents.OutboxEvent
import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OutboxEventsDataStore : PanacheRepository<OutboxEventEntity> {
	fun createEvent(createOutboxEvent: CreateOutboxEvent): Uni<OutboxEvent> {
		val outboxEventEntity = createOutboxEvent.toOutboxEventEntity()
		return persist(outboxEventEntity)
			.map {
				it.toOutboxEvent()
			}
	}
}