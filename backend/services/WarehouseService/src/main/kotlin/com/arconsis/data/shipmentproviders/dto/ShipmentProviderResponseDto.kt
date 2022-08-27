package com.arconsis.data.shipmentproviders.dto

import com.arconsis.domain.shipmentproviders.ShipmentProvider
import com.fasterxml.jackson.annotation.JsonProperty

data class ShipmentProviderResponseDto(
	@JsonProperty("rates") val providers: List<ShipmentProviderRateResponseDto>,
)

data class ShipmentProviderRateResponseDto(
	@JsonProperty("object_id") val providerId: String,
	@JsonProperty("amount") val price: String,
	@JsonProperty("currency") val currency: String,
	@JsonProperty("provider") val name: String,
	@JsonProperty("provider_image_75") val image: String,
	@JsonProperty("carrier_account") val carrierAccount: String,
)

fun ShipmentProviderRateResponseDto.toShipmentProvider() = ShipmentProvider(
	providerId = providerId,
	price = price,
	currency = currency,
	name = name,
	image = image,
	carrierAccount = carrierAccount
)