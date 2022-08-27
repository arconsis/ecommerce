package com.arconsis.presentation.http.baskets.dto

import java.math.BigDecimal
import javax.validation.constraints.NotBlank

data class AddShippingProviderDto(
	@field:NotBlank
	val providerId: String,
	@field:NotBlank
	val price: BigDecimal,
	@field:NotBlank
	val name: String,
)
