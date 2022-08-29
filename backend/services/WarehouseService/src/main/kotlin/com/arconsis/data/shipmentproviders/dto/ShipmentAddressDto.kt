package com.arconsis.data.shipmentproviders.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ShipmentAddressDto(
	@JsonProperty("name") val name: String,
	@JsonProperty("street1") val street1: String,
	@JsonProperty("city") val city: String,
	@JsonProperty("zip") val zip: String,
	@JsonProperty("country") val country: String,
	@JsonProperty("state") val state: String
)
