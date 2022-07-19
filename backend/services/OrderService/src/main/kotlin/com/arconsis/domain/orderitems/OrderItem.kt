package com.arconsis.domain.orderitems

import java.math.BigDecimal
import java.util.*

data class OrderItem(
	val itemId: UUID,
	val productId: UUID,
	val orderId: UUID,
	val price: BigDecimal,
	val currency: String,
	val quantity: Int
)

data class CreateOrderItem(
	val productId: UUID,
	val orderId: UUID?,
	val price: BigDecimal,
	val currency: String,
	val quantity: Int
)