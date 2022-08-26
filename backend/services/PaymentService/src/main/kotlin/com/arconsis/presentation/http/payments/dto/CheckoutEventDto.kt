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
	@JsonProperty("id") val pspReferenceId: String,
	@JsonProperty("metadata") val metadata: CheckoutEventMetadataDto,
	@JsonProperty("failure_code") val paymentErrorCode: String?,
	@JsonProperty("failure_message") val paymentErrorMessage: String?,
)

data class CheckoutEventMetadataDto(
	@JsonProperty("order_id") val orderId: String
)

data class CreateCheckoutEventDto(
	@JsonProperty("type") val type: String,
	@JsonProperty("orderId") val orderId: String,
	@JsonProperty("pspData") val pspData: String,
	@JsonProperty("pspReferenceId") val pspReferenceId: String,
	@JsonProperty("paymentErrorCode") val paymentErrorCode: String?,
	@JsonProperty("paymentErrorMessage") val paymentErrorMessage: String?,
)