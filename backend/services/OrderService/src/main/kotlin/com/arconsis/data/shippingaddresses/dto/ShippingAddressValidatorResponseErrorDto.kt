package com.arconsis.data.shippingaddresses.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ShippingAddressValidatorResponseErrorDto(
	@JsonProperty("body") val body: ShippingAddressValidatorErrorDetailsDto,
)

data class ShippingAddressValidatorErrorDetailsDto(
	@JsonProperty("error") val error: String,
)
