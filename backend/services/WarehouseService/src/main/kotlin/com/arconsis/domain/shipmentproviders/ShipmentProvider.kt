package com.arconsis.domain.shipmentproviders

import com.arconsis.domain.orders.SupportedCurrencies

data class ShipmentProvider(
	val providerId: String,
	val name: String,
	val rate: ShipmentProviderRate,
	val carrier: String,
	val carrierName: String,
	val carrierImage: String,
	val carrierAccount: String,
	val description: String
)

data class ShipmentProviderRate(
	val price: String,
	val currency: SupportedCurrencies,
)