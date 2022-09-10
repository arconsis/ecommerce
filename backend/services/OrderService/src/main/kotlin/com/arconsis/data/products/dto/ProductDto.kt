package com.arconsis.data.products.dto

import com.arconsis.domain.orders.SupportedCurrencies
import com.arconsis.domain.products.Product
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.util.*

// TODO: when a new product is added -> fire event and store product details in order service db to avoid rest
data class ProductDto(
	@JsonProperty("productId") val productId: UUID,
	@JsonProperty("thumbnail") val thumbnail: String,
	@JsonProperty("name") val name: String,
	@JsonProperty("slug") val slug: String,
	@JsonProperty("sku") val sku: String,
	@JsonProperty("description") val description: String,
	@JsonProperty("currency") val currency: SupportedCurrencies,
	@JsonProperty("price") val price: BigDecimal
)

fun ProductDto.toProduct() = Product(
	productId = productId,
	thumbnail = thumbnail,
	name = name,
	slug = slug,
	sku = sku,
	description = description,
	currency = currency,
	price = price
)