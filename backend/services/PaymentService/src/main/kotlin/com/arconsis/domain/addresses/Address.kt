package com.arconsis.domain.addresses

import java.util.*

data class Address(
    val addressId: UUID,
    val name: String,
    val address: String,
    val houseNumber: String,
    val countryCode: String,
    val postalCode: String,
    val city: String,
    val phone: String,
    val isBilling: Boolean,
    val isSelected: Boolean,
)
