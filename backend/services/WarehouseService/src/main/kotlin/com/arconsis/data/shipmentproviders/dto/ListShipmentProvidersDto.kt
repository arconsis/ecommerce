package com.arconsis.data.shipmentproviders.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ListShipmentProvidersDto(
	@JsonProperty("address_from") val addressFrom: ShipmentAddressDto,
	@JsonProperty("address_to") val addressTo: ShipmentAddressDto,
	@JsonProperty("parcels") val parcels: List<ParcelDto>,
	@JsonProperty("async") val async: Boolean = false,
)

data class ParcelDto(
	@JsonProperty("length") val length: String = "5",
	@JsonProperty("width") val width: String = "5",
	@JsonProperty("height") val height: String = "5",
	@JsonProperty("distance_unit") val distanceUnit: String = "in",
	@JsonProperty("weight") val weight: String = "2",
	@JsonProperty("mass_unit") val massUnit: String = "lb"
)