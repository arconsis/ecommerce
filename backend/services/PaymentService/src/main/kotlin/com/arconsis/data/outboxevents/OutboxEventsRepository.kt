package com.arconsis.data.outboxevents

import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.outboxevents.OutboxEvent
import io.smallrye.mutiny.Uni
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OutboxEventsRepository (val outboxEventsDataStore: OutboxEventsDataStore) {
    fun createEvent(createOutboxEvent: CreateOutboxEvent): Uni<OutboxEvent> {
        return outboxEventsDataStore.createEvent(createOutboxEvent)
    }
}