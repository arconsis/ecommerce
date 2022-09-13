package com.arconsis.data.products.dto

import com.arconsis.domain.orders.SupportedCurrencies
import com.arconsis.domain.products.Product
import com.arconsis.domain.products.ProductMedia
import com.arconsis.domain.products.ProductMediaType
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.util.*

// TODO: when a new product is added -> fire event and store product details in order service db to avoid rest
data class ProductDto(
	@JsonProperty("productId") val productId: UUID,
	@JsonProperty("name") val name: String,
	@JsonProperty("slug") val slug: String,
	@JsonProperty("sku") val sku: String,
	@JsonProperty("description") val description: String,
	@JsonProperty("currency") val currency: SupportedCurrencies,
	@JsonProperty("price") val price: BigDecimal,
	@JsonProperty("gallery") val gallery: List<ProductMediaDto>,
)

data class ProductMediaDto(
	@JsonProperty("mediaId") val mediaId: UUID,
	@JsonProperty("productId") val productId: UUID,
	@JsonProperty("original") val original: String,
	@JsonProperty("thumbnail") val thumbnail: String,
	@JsonProperty("type") val type: ProductMediaType,
	@JsonProperty("isPrimary") val isPrimary: Boolean,
)

fun ProductDto.toProduct() = Product(
	productId = productId,
	name = name,
	slug = slug,
	sku = sku,
	description = description,
	currency = currency,
	price = price,
	gallery = gallery.map {
		ProductMedia(
			mediaId = it.mediaId,
			productId = it.productId,
			original = it.original,
			thumbnail = it.thumbnail,
			type = it.type,
			isPrimary = it.isPrimary
		)
	}
)