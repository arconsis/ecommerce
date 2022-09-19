package com.arconsis.data.addresses.dto

import com.arconsis.domain.addresses.CreateAddress
import com.arconsis.domain.addresses.SupportedCountryCode
import com.fasterxml.jackson.annotation.JsonProperty

data class AddressValidationBodyRequestDto(
	@JsonProperty("firstName") val firstName: String,
	@JsonProperty("lastName") val lastName: String,
	@JsonProperty("address") val address: String,
	@JsonProperty("houseNumber") val houseNumber: String,
	@JsonProperty("countryCode") val countryCode: SupportedCountryCode,
	@JsonProperty("postalCode") val postalCode: String,
	@JsonProperty("city") val city: String,
	@JsonProperty("phone") val phone: String,
	@JsonProperty("state") val state: String,
)

fun CreateAddress.toAddressValidationBodyRequestDto() = AddressValidationBodyRequestDto(
	firstName = firstName,
	lastName = lastName,
	address = address,
	houseNumber = houseNumber,
	countryCode = countryCode,
	postalCode = postalCode,
	city = city,
	phone = phone,
	state = state
)