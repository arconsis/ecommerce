package com.arconsis.presentation.http.products.dto.response

import com.arconsis.domain.products.Product
import com.fasterxml.jackson.annotation.JsonProperty

data class ListProductsResponseDto(
	@JsonProperty("data") val data: List<Product>,
	@JsonProperty("pagination") val pagination: ListProductsPaginationResponseDto
)

data class ListProductsPaginationResponseDto(
	@JsonProperty("total") val total: Int,
	@JsonProperty("limit") val limit: Int,
	@JsonProperty("offset") val offset: Int
)