package com.arconsis.presentation.http.orders.dto

import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.OrderPaymentMethodType
import com.arconsis.domain.orders.SupportedCurrencies
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.util.*
import javax.validation.constraints.NotBlank

data class CreateOrderDto(
    @field:NotBlank
    @JsonProperty("userId") val userId: UUID,
    @field:NotBlank
    @JsonProperty("basketId") val basketId: UUID,
)

fun CreateOrderDto.toCreateOrder(
    totalPrice: BigDecimal,
    priceBeforeTax: BigDecimal,
    tax: String,
    currency: SupportedCurrencies,
    pspToken: String,
    paymentMethodType: OrderPaymentMethodType,
) = CreateOrder(
    userId = userId,
    totalPrice = totalPrice,
    priceBeforeTax = priceBeforeTax,
    tax = tax,
    currency = currency,
    basketId = basketId,
    pspToken = pspToken,
    paymentMethodType = paymentMethodType
)