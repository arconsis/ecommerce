package com.arconsis.domain.baskets

import com.arconsis.domain.orders.OrderItem
import java.math.BigDecimal
import java.util.*

data class BasketItem(
	val itemId: UUID,
	val basketId: UUID,
	val productId: UUID,
	val thumbnail: String,
	val productName: String,
	val description: String,
	val currency: String,
	val price: BigDecimal,
	val quantity: Int
)

data class CreateBasketItem(
	val productId: UUID,
	val thumbnail: String,
	val productName: String,
	val description: String,
	val currency: String,
	val price: BigDecimal,
	val quantity: Int
)

fun BasketItem.toOrderItem(orderId: UUID) = OrderItem(
	itemId = itemId,
	productId = productId,
	orderId = orderId,
	price = price,
	currency = currency,
	quantity = quantity,
	productName = productName
)
