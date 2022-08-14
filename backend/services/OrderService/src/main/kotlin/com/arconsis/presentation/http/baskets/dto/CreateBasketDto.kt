package com.arconsis.presentation.http.baskets.dto

import com.arconsis.common.TAX_RATE
import com.arconsis.domain.baskets.CreateBasket
import com.arconsis.domain.baskets.CreateBasketItem
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.util.*
import javax.validation.constraints.NotBlank

data class CreateBasketDto(
	@field:NotBlank
	@JsonProperty("userId") val userId: UUID,
	@field:NotBlank
	@JsonProperty("items") val items: List<CreateBasketItemDto>,
)

data class CreateBasketItemDto(
	@field:NotBlank
	@JsonProperty("productId") val productId: UUID,
	@field:NotBlank
	@JsonProperty("quantity") val quantity: Int,
)

fun CreateBasketDto.toCreateBasket(items: List<CreateBasketItem>) = CreateBasket(
	userId = userId,
	totalPrice = items.calculateBasketTotalPriceAfterTax(),
	priceBeforeTax = items.calculateBasketTotalPrice(),
	tax = TAX_RATE,
	currency = items[0].currency,
	items = items
)

private fun List<CreateBasketItem>.calculateBasketTotalPrice(): BigDecimal {
	return this.fold(BigDecimal(0)) { acc, item ->
		acc + (item.price * BigDecimal(item.quantity))
	}.setScale(2)
}

private fun List<CreateBasketItem>.calculateBasketTotalPriceAfterTax(): BigDecimal {
	val price = this.calculateBasketTotalPrice()
	return price.multiply(BigDecimal(1) + BigDecimal(TAX_RATE)).setScale(2)
}