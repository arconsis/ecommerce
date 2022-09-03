package com.arconsis.data.shipmentproviders.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ShipmentProviderResponseDto(
	@JsonProperty("object_id") val providerId: String,
	@JsonProperty("carrier") val carrier: String,
	@JsonProperty("carrier_name") val carrierName: String,
	@JsonProperty("carrier_images") val carrierImages: ShipmentProviderImagesResponseDto,
	@JsonProperty("service_levels") val serviceLevels: List<ShipmentProviderServiceGroupLevelResponseDto>,
	@JsonProperty("active") val active: Boolean,
)

data class ShipmentProviderImagesResponseDto(
	@JsonProperty("200") val image200: String,
	@JsonProperty("75") val image75: String,
)

data class ShipmentProviderServiceGroupLevelResponseDto(
	@JsonProperty("name") val carrierName: String,
	@JsonProperty("token") val token: String,
)