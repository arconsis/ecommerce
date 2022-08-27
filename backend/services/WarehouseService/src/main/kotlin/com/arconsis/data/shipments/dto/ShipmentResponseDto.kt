package com.arconsis.data.shipments.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ShipmentResponseDto(
	@JsonProperty("object_id") val externalShipmentId: String,
)
