package com.arconsis.domain.products

import com.arconsis.domain.orders.SupportedCurrencies
import java.math.BigDecimal
import java.util.*

data class Product(
	val productId: UUID,
	val thumbnail: String,
	val productName: String,
	val description: String,
	val price: BigDecimal,
	val currency: SupportedCurrencies,
	val size: String,
	val category: String,
	val isOrderable: Boolean?,
)

data class CreateProduct(
	val thumbnail: String,
	val productName: String,
	val description: String,
	val price: BigDecimal,
	val currency: SupportedCurrencies,
	val size: String,
	val category: String,
)