package com.arconsis.domain.shipmentproviders

data class ShipmentProvider(
	val providerId: String,
	val price: String,
	val currency: String,
	val name: String,
	val image: String,
	val carrierAccount: String
)
