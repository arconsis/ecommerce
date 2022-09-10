package com.arconsis.domain.baskets

import com.arconsis.domain.orders.OrderItem
import com.arconsis.domain.orders.SupportedCurrencies
import java.math.BigDecimal
import java.util.*

data class BasketItem(
	val itemId: UUID,
	val basketId: UUID,
	val productId: UUID,
	val thumbnail: String,
	val name: String,
	val description: String,
	val currency: SupportedCurrencies,
	val price: BigDecimal,
	val quantity: Int
)

data class CreateBasketItem(
	val productId: UUID,
	val thumbnail: String,
	val name: String,
	val slug: String,
	val sku: String,
	val description: String,
	val currency: SupportedCurrencies,
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
	name = name,
	thumbnail = thumbnail
)
