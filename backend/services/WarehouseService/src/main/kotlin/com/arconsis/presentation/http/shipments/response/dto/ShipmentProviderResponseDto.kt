package com.arconsis.presentation.http.shipments.response.dto

import com.arconsis.domain.orders.SupportedCurrencies
import com.arconsis.domain.shipmentproviders.ShipmentProvider
import com.fasterxml.jackson.annotation.JsonProperty

data class ShipmentProviderResponseDto(
	@JsonProperty("providerId") val providerId: String,
	@JsonProperty("name") val name: String,
	@JsonProperty("rate") val rate: ShipmentProviderRateResponseDto,
	@JsonProperty("carrier") val carrier: String,
	@JsonProperty("carrierName") val carrierName: String,
	@JsonProperty("carrierImage") val carrierImage: String,
	@JsonProperty("carrierAccount") val carrierAccount: String,
	@JsonProperty("description") val description: String
)

data class ShipmentProviderRateResponseDto(
	@JsonProperty("price") val price: String,
	@JsonProperty("currency") val currency: SupportedCurrencies,
)

fun ShipmentProvider.toShipmentProviderResponseDto() = ShipmentProviderResponseDto(
	providerId = providerId,
	name = name,
	rate = ShipmentProviderRateResponseDto(
		price = rate.price,
		currency = rate.currency,
	),
	carrier = carrier,
	carrierName = carrierName,
	carrierImage = carrierImage,
	carrierAccount = carrierAccount,
	description = description
)