package com.arconsis.presentation.http.payments.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CheckoutEventDto(
	@JsonProperty("type") val type: String,
	@JsonProperty("data") val data: CheckoutEventDataDto,
)

data class CheckoutEventDataDto (
	@JsonProperty("object") val entity: CheckoutEventObjectDto
)

data class CheckoutEventObjectDto (
	@JsonProperty("metadata") val metadata: CheckoutEventMetadataDto,
)

data class CheckoutEventMetadataDto(
	@JsonProperty("order_id") val orderId: String
)

data class CreateCheckoutEventDto(
	@JsonProperty("type") val type: String,
	@JsonProperty("orderId") val orderId: String,
	@JsonProperty("metadata") val metadata: String,
)