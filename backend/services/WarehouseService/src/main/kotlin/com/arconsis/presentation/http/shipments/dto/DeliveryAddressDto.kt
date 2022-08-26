package com.arconsis.presentation.http.shipments.dto

data class DeliveryAddressDto(
	val name: String,
	val address: String,
	val houseNumber: String,
	val countryCode: String,
	val postalCode: String,
	val city: String,
	val phone: String
)