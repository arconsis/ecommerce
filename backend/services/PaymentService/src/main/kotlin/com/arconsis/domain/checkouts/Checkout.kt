package com.arconsis.domain.checkouts

data class Checkout(
    // psp ref
    val checkoutSessionId: String,
    val checkoutUrl: String,
)
