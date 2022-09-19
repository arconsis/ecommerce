package com.arconsis.data.addresses.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AddressValidatorResponseErrorDto(
	@JsonProperty("body") val body: AddressValidatorErrorDetailsDto,
)

data class AddressValidatorErrorDetailsDto(
	@JsonProperty("error") val error: String,
)
