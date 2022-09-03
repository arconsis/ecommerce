package com.arconsis.data.shippingproviders.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ShippingProviderResponseDto(
	@JsonProperty("object_id") val providerId: String,
	@JsonProperty("carrier") val carrier: String,
	@JsonProperty("carrier_name") val carrierName: String,
	@JsonProperty("carrier_images") val carrierImages: ShippingProviderImagesResponseDto,
	@JsonProperty("service_levels") val serviceLevels: List<ShippingProviderServiceGroupLevelResponseDto>,
	@JsonProperty("active") val active: Boolean,
)

data class ShippingProviderImagesResponseDto(
	@JsonProperty("200") val image200: String,
	@JsonProperty("75") val image75: String,
)

data class ShippingProviderServiceGroupLevelResponseDto(
	@JsonProperty("name") val carrierName: String,
	@JsonProperty("token") val token: String,
)