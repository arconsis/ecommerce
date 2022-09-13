package com.arconsis.domain.products

import com.arconsis.domain.orders.SupportedCurrencies
import java.math.BigDecimal
import java.util.*

data class Product(
	val productId: UUID,
	val name: String,
	val slug: String,
	val sku: String,
	val description: String,
	val currency: SupportedCurrencies,
	val price: BigDecimal,
	val gallery: List<ProductMedia>
)

data class ProductMedia(
	val mediaId: UUID,
	val productId: UUID,
	val original: String,
	val thumbnail: String,
	val type: ProductMediaType,
	val isPrimary: Boolean
)

enum class ProductMediaType {
	image,
	video
}