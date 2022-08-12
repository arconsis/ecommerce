package com.arconsis.domain.baskets

import java.math.BigDecimal
import java.util.*

data class Basket(
	val basketId: UUID,
	val userId: UUID,
	val totalPrice: BigDecimal,
	val tax: String,
	val priceBeforeTax: BigDecimal,
	val currency: String,
	val items: List<BasketItem>
)

data class CreateBasket(
	val userId: UUID,
	val totalPrice: BigDecimal,
	val priceBeforeTax: BigDecimal,
	val tax: String,
	val currency: String,
	val items: List<CreateBasketItem>
)