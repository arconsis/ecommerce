package com.arconsis.presentation.http.orders.dto

import com.arconsis.domain.orderitems.CreateOrderItem
import com.arconsis.domain.orders.CreateOrder
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.util.*
import javax.validation.constraints.NotBlank

data class CreateOrderDto(
    @field:NotBlank
    @JsonProperty("userId") val userId: UUID,
    @field:NotBlank
    @JsonProperty("amount") val amount: BigDecimal,
    @field:NotBlank
    @JsonProperty("currency") val currency: String,
    @field:NotBlank
    @JsonProperty("items") val items: List<CreateOrderItemDto>,
)

data class CreateOrderItemDto(
    @field:NotBlank
    @JsonProperty("productId") val productId: UUID,
    @field:NotBlank
    @JsonProperty("price") val price: BigDecimal,
    @field:NotBlank
    @JsonProperty("currency") val currency: String,
    @field:NotBlank
    @JsonProperty("quantity") val quantity: Int,
)

fun CreateOrderDto.toCreateOrder() = CreateOrder(
    userId = userId,
    amount = amount,
    currency = currency,
    items = items.map {
        CreateOrderItem(
            orderId = null,
            productId = it.productId,
            price = it.price,
            currency = it.currency,
            quantity = it.quantity
        )
    }
)