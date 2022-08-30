package com.arconsis.domain.shipmentproviders

import com.arconsis.domain.orders.SupportedCurrencies

data class ShipmentProvider(
	val providerId: String,
	val externalShipmentId: String,
	val price: String,
	val currency: SupportedCurrencies,
	val name: String,
	val image: String,
	val carrierAccount: String
)
