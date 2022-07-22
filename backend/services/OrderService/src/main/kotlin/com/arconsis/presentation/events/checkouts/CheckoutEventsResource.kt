package com.arconsis.presentation.events.checkouts

import com.arconsis.domain.checkout.Checkout
import com.arconsis.domain.checkout.CheckoutsService
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Incoming
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CheckoutEventsResource(
    private val checkoutsService: CheckoutsService,
    private val objectMapper: ObjectMapper
) {
    @Incoming("payments-in")
    fun consumePaymentEvents(checkoutRecord: Record<String, CheckoutEventDto>): Uni<Void> {
        val checkoutEventDto = checkoutRecord.value()
        val eventId = UUID.fromString(checkoutEventDto.payload.currentValue.id)
        val checkout = objectMapper.readValue(
            checkoutEventDto.payload.currentValue.toOutboxEvent().payload,
            Checkout::class.java
        )
        return checkoutsService.handleCheckoutEvents(eventId, checkout)
            .onFailure()
            .recoverWithNull()
    }
}