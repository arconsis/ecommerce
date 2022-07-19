package com.arconsis.data.processedevents

import com.arconsis.domain.processedevents.ProcessedEvent
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProcessedEventsRepository(val proceedEventsDataStore: ProceedEventsDataStore) {
    fun createEvent(event: ProcessedEvent, session: Mutiny.Session): Uni<ProcessedEvent> {
        return proceedEventsDataStore.createEvent(event, session)
    }
}