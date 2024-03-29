package com.arconsis.domain.orders

import java.math.BigDecimal
import java.util.*

data class OrderItem(
	val itemId: UUID,
	val productId: UUID,
	val orderId: UUID,
	val price: BigDecimal,
	val currency: SupportedCurrencies,
	val quantity: Int,
	val name: String,
	val thumbnail: String,
)
