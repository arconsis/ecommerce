package com.arconsis.data.shipmentlabels.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ShipmentLabelResponseDto(
	@JsonProperty("object_id") val externalShipmentLabelId: String,
)
