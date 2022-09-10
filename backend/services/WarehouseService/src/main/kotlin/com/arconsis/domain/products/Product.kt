package com.arconsis.domain.products

import com.arconsis.domain.orders.SupportedCurrencies
import java.math.BigDecimal
import java.util.*

data class Product(
	val productId: UUID,
	val name: String,
	val slug: String,
	val sku: String,
	val thumbnail: String,
	val description: String,
	val price: BigDecimal,
	val currency: SupportedCurrencies,
	val tags: String,
	val inStock: Boolean?,
	val quantityInStock: Int?,
	val dimensions: ProductDimensions,
)

data class CreateProduct(
	val thumbnail: String,
	val name: String,
	val description: String,
	val price: BigDecimal,
	val currency: SupportedCurrencies,
	val tags: String,
	val dimensions: ProductDimensions,
	val quantityInStock: Int?,
)

data class ProductDimensions(
	val size: ProductSize,
	val weight: ProductWeight
)

data class ProductWeight(
	val value: Long,
	val unit: ProductWeightUnit
)

enum class ProductWeightUnit {
	lb,
	kg
}

data class ProductSize(
	val height: Long?,
	val width: Long?,
	val length: Long?,
	val unit: ProductSizeUnit?
)

enum class ProductSizeUnit {
	cm,
	m
}