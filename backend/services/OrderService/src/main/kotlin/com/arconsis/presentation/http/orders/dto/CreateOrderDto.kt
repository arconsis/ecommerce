package com.arconsis.presentation.http.orders.dto

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
    @JsonProperty("basketId") val basketId: UUID,
)

fun CreateOrderDto.toCreateOrder() = CreateOrder(
    userId = userId,
    amount = amount,
    currency = currency,
    basketId = basketId
)