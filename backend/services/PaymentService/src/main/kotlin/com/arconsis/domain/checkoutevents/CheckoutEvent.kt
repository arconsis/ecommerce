package com.arconsis.domain.checkoutevents

import java.util.*

data class CheckoutEvent(
	val eventId: UUID,
	val type: String,
	val checkoutId: UUID,
	val pspData: String,
	val pspReferenceId: String
)

data class CreateCheckoutEvent(
	val type: String,
	val checkoutId: UUID,
	val pspData: String,
	val pspReferenceId: String
)
