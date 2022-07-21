package com.arconsis.data.checkoutevents

import com.arconsis.domain.checkoutevents.CheckoutEvent
import com.arconsis.domain.checkoutevents.CreateCheckoutEvent
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CheckoutEventsDataStore {
    fun createCheckoutEvent(event: CreateCheckoutEvent, session: Mutiny.Session): Uni<CheckoutEvent> {
        val entity = event.toCheckoutEntity()
        return session.persist(entity)
            .map { entity.toCheckoutEvent() }
    }
}