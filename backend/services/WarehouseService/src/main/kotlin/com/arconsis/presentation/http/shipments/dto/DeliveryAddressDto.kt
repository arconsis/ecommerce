package com.arconsis.presentation.http.shipments.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class DeliveryAddressDto(
	@field:NotBlank
	val firstName: String,
	@field:NotBlank
	val lastName: String,
	@field:NotBlank
	val address: String,
	@field:NotBlank
	val houseNumber: String,
	@field:NotNull
	val countryCode: String,
	@field:NotBlank
	val postalCode: String,
	@field:NotBlank
	val city: String,
	@field:NotBlank
	val phone: String,
	@field:NotBlank
	val state: String
)