package com.arconsis.presentation.events.products.dto.request

import com.arconsis.domain.orders.SupportedCurrencies
import com.arconsis.domain.products.Product
import com.arconsis.domain.products.ProductMediaType
import java.math.BigDecimal
import java.util.*

data class ProductEventDto(
	val productId: UUID,
	val name: String,
	val slug: String,
	val sku: String,
	val description: String,
	val currency: SupportedCurrencies,
	val price: BigDecimal,
	val gallery: List<ProductEventMediaDto>
)

data class ProductEventMediaDto(
	val original: String,
	val thumbnail: String,
	val type: ProductMediaType,
	val isPrimary: Boolean
)

fun ProductEventDto.toProduct() = Product(
	productId = productId,
	slug = slug,
	sku = sku,
	name = name,
	description = description,
	price = price,
	currency = currency,
	thumbnail = gallery.first { it.isPrimary }.thumbnail
)
