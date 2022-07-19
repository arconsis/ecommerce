package com.arconsis.data.outboxevents

import com.arconsis.domain.outboxevents.CreateOutboxEvent
import com.arconsis.domain.outboxevents.OutboxEvent
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OutboxEventsRepository(val outboxEventsDataStore: OutboxEventsDataStore) {

    fun createEvent(createOutboxEvent: CreateOutboxEvent, session: Mutiny.Session): Uni<OutboxEvent> {
        return outboxEventsDataStore.createEvent(createOutboxEvent, session)
    }
}