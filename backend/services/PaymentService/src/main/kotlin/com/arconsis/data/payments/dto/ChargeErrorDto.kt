package com.arconsis.data.payments.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ChargeErrorResponseDto(
	@JsonProperty("error") val error: ChargeErrorDto,
)

data class ChargeErrorDto(
	@JsonProperty("id") val chargeId: String,
	@JsonProperty("code") val code: String,
	@JsonProperty("decline_code") val declineCode: String,
)
