package com.arconsis.data.shippingproviders.dto

import com.arconsis.domain.orders.SupportedCurrencies
import com.fasterxml.jackson.annotation.JsonProperty

data class ShippingProviderRateResponseDto(
	@JsonProperty("object_id") val providerId: String,
	@JsonProperty("amount") val price: String,
	@JsonProperty("currency") val currency: SupportedCurrencies,
	@JsonProperty("provider") val name: String,
	@JsonProperty("provider_image_75") val image: String,
	@JsonProperty("carrier_account") val carrierAccount: String,
	@JsonProperty("shipment") val shipmentId: String,
)
