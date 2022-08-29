package com.arconsis.domain.shippingaddresses

data class ShippingAddress(
    val firstName: String,
    val lastName: String,
    val address: String,
    val houseNumber: String,
    val countryCode: String,
    val postalCode: String,
    val city: String,
    val phone: String,
    val state: String,
)
