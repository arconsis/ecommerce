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
	@JsonProperty("price") val price: BigDecimal,
	@field:NotBlank
	@JsonProperty("currency") val currency: String,
	@field:NotBlank
	@JsonProperty("quantity") val quantity: Int,
	@field:NotBlank
	@JsonProperty("thumbnail") val thumbnail: String,
	@field:NotBlank
	@JsonProperty("productName") val productName: String,
	@field:NotBlank
	@JsonProperty("description") val description: String,
)

fun CreateBasketDto.toCreateBasket() = CreateBasket(
	userId = userId,
	totalPrice = calculateBasketTotalPriceAfterTax(),
	priceBeforeTax = calculateBasketTotalPrice(),
	tax = TAX_RATE,
	currency = items[0].currency,
	items = items.map {
		CreateBasketItem(
			productId = it.productId,
			price = it.price,
			currency = it.currency,
			quantity = it.quantity,
			thumbnail = it.thumbnail,
			productName = it.productName,
			description = it.description
		)
	}
)

private fun CreateBasketDto.calculateBasketTotalPrice(): BigDecimal {
	return items.fold(BigDecimal(0)) { acc, item ->
		acc + (item.price * BigDecimal(item.quantity))
	}.setScale(2)
}

private fun CreateBasketDto.calculateBasketTotalPriceAfterTax(): BigDecimal {
	val price = this.calculateBasketTotalPrice()
	return price.multiply(BigDecimal(1) + BigDecimal(TAX_RATE)).setScale(2)
}