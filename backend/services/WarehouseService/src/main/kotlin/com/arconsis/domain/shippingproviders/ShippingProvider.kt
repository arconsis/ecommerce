package com.arconsis.domain.shippingproviders

import com.arconsis.domain.orders.SupportedCurrencies

data class ShippingProvider(
	val providerId: String,
	val name: String,
	val rate: ShippingProviderRate,
	val carrier: String,
	val carrierName: String,
	val carrierImage: String,
	val carrierAccount: String,
	val description: String
)

data class ShippingProviderRate(
	val price: String,
	val currency: SupportedCurrencies,
)