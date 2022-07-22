package com.arconsis.domain.payments

import java.util.*

data class Payment(
    val paymentId: UUID?,
    val pspReferenceId: String,
    val checkoutId: UUID
)

data class CreatePayment(
    val checkoutId: UUID,
    val pspReferenceId: String,
)