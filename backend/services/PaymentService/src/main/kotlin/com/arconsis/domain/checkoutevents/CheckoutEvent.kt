package com.arconsis.domain.checkoutevents

import java.util.*

data class CheckoutEvent(
	val eventId: UUID,
	val type: String,
	val paymentId: UUID,
	val metadata: String
)

data class CreateCheckoutEvent(
	val type: String,
	val paymentId: UUID,
	val metadata: String
)
