package com.arconsis.data.shipmentlabels.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateShipmentLabelDto(
	@JsonProperty("shipment") val shipment: CreateShipmentDto,
	@JsonProperty("carrier_account") val providerId: String,
	@JsonProperty("servicelevel_token") val serviceLevelToken: String,
	@JsonProperty("label_file_type") val labelFileType: String = "PDF",
	@JsonProperty("async") val async: Boolean = false,
)

data class CreateShipmentDto(
	@JsonProperty("address_from") val addressFrom: ShipmentAddressDto,
	@JsonProperty("address_to") val addressTo: ShipmentAddressDto,
	@JsonProperty("parcels") val parcels: List<ParcelDto>,
)

data class ParcelDto(
	@JsonProperty("length") val length: String = "45",
	@JsonProperty("width") val width: String = "35",
	@JsonProperty("height") val height: String = "16",
	@JsonProperty("distance_unit") val distanceUnit: String = "cm",
	@JsonProperty("weight") val weight: String = "2",
	@JsonProperty("mass_unit") val massUnit: String = "kg"
)