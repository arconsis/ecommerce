package com.arconsis.domain.orderitems

import java.math.BigDecimal
import java.util.*

data class OrderItem(
	val itemId: UUID,
	val productId: UUID,
	val orderId: UUID,
	val price: BigDecimal,
	val currency: SupportedCurrencies,
	val quantity: Int,
	val productName: String,
	val thumbnail: String,
)
