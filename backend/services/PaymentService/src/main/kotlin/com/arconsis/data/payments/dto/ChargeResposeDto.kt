package com.arconsis.data.payments.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.util.*

data class ChargeResposeDto(
	@JsonProperty("id") val chargeId: String,
)
