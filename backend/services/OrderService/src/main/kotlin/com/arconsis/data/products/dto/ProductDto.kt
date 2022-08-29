package com.arconsis.data.products.dto

import com.arconsis.domain.orders.SupportedCurrencies
import com.arconsis.domain.products.Product
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.util.*

data class ProductDto(
	@JsonProperty("productId") val productId: UUID,
	@JsonProperty("thumbnail") val thumbnail: String,
	@JsonProperty("productName") val productName: String,
	@JsonProperty("description") val description: String,
	@JsonProperty("currency") val currency: SupportedCurrencies,
	@JsonProperty("price") val price: BigDecimal
)

fun ProductDto.toProduct() = Product(
	productId = productId,
	thumbnail = thumbnail,
	productName = productName,
	description = description,
	currency = currency,
	price = price
)