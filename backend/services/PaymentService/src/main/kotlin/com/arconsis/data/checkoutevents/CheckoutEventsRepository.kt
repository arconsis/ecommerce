package com.arconsis.data.checkoutevents

import com.arconsis.domain.checkoutevents.CheckoutEvent
import com.arconsis.domain.checkoutevents.CreateCheckoutEvent
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CheckoutEventsRepository(private val checkoutEventsDataStore: CheckoutEventsDataStore) {
	fun createCheckoutEvent(event: CreateCheckoutEvent, session: Mutiny.Session): Uni<CheckoutEvent> {
		return checkoutEventsDataStore.createCheckoutEvent(event, session)
	}
}