package com.arconsis.domain.products

import com.arconsis.domain.baskets.CreateBasketItem
import java.math.BigDecimal
import java.util.*

data class Product(
	val productId: UUID,
	val thumbnail: String,
	val productName: String,
	val description: String,
	val currency: String,
	val price: BigDecimal,
)
