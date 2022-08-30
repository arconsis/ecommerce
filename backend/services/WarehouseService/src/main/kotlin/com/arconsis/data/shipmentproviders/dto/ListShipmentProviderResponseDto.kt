package com.arconsis.data.shipmentproviders.dto

import com.arconsis.domain.orders.SupportedCurrencies
import com.arconsis.domain.shipmentproviders.ShipmentProvider
import com.fasterxml.jackson.annotation.JsonProperty

data class ListShipmentProviderResponseDto(
	@JsonProperty("rates") val providers: List<ShipmentProviderRateResponseDto>,
)

data class ShipmentProviderRateResponseDto(
	@JsonProperty("object_id") val providerId: String,
	@JsonProperty("amount") val price: String,
	@JsonProperty("currency") val currency: SupportedCurrencies,
	@JsonProperty("provider") val name: String,
	@JsonProperty("provider_image_75") val image: String,
	@JsonProperty("carrier_account") val carrierAccount: String,
	@JsonProperty("shipment") val shipmentId: String,
)

fun ShipmentProviderRateResponseDto.toShipmentProvider() = ShipmentProvider(
	providerId = providerId,
	price = price,
	currency = currency,
	name = name,
	image = image,
	carrierAccount = carrierAccount,
	externalShipmentId = shipmentId
)