package com.arconsis.domain.baskets

import java.math.BigDecimal
import java.util.*

data class Basket(
	val basketId: UUID,
	val userId: UUID,
	val amount: BigDecimal,
	val currency: String,
	val items: List<BasketItem>
)

data class CreateBasket(
	val userId: UUID,
	val amount: BigDecimal,
	val currency: String,
	val items: List<CreateBasketItem>
)