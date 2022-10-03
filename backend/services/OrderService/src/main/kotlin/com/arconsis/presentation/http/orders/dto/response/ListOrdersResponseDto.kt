package com.arconsis.presentation.http.orders.dto.response

import com.arconsis.domain.orders.Order
import com.fasterxml.jackson.annotation.JsonProperty

data class ListOrdersResponseDto(
	@JsonProperty("data") val data: List<Order>,
	@JsonProperty("pagination") val pagination: ListOrdersPaginationResponseDto
)

data class ListOrdersPaginationResponseDto(
	@JsonProperty("total") val total: Int,
	@JsonProperty("limit") val limit: Int,
	@JsonProperty("offset") val offset: Int
)