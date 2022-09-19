package com.arconsis.data.shippingaddresses.dto

import com.arconsis.domain.shippingaddresses.SupportedCountryCode
import com.fasterxml.jackson.annotation.JsonProperty

data class ShippingAddressValidationBodyRequestDto(
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
