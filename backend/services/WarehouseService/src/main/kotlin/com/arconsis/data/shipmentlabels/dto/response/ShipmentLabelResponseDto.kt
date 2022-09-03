package com.arconsis.data.shipmentlabels.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class ShipmentLabelResponseDto(
	@JsonProperty("object_id") val externalShipmentLabelId: String,
	@JsonProperty("rate") val rate: ShipmentLabelRateResponseDto,
)

data class ShipmentLabelRateResponseDto(
	@JsonProperty("object_id") val externalRateId: String,
)