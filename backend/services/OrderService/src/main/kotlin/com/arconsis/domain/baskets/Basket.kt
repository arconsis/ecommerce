package com.arconsis.domain.baskets

import com.arconsis.domain.shippingaddresses.ShippingAddress
import com.arconsis.domain.orders.OrderPaymentMethod
import com.arconsis.domain.orders.OrderPrices
import com.arconsis.domain.orders.OrderShipmentProvider
import com.arconsis.domain.orders.SupportedCurrencies
import java.math.BigDecimal
import java.util.*

data class Basket(
	val basketId: UUID,
	val userId: UUID,
	val prices: OrderPrices,
	val items: List<BasketItem>,
	val shippingShippingAddress: ShippingAddress? = null,
	val billingShippingAddress: ShippingAddress? = null,
	val paymentMethod: OrderPaymentMethod? = null,
	val isOrderable: Boolean,
	// shipmentProvider
	val shipmentProvider: OrderShipmentProvider?
)

data class CreateBasket(
	val userId: UUID,
	val totalPrice: BigDecimal,
	val priceBeforeTax: BigDecimal,
	val productPrice: BigDecimal,
	val shippingPrice: BigDecimal,
	val tax: String,
	val currency: SupportedCurrencies,
	val items: List<CreateBasketItem>
)