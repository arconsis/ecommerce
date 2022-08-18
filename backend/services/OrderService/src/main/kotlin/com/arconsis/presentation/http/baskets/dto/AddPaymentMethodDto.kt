package com.arconsis.presentation.http.baskets.dto

import com.arconsis.domain.orders.OrderPaymentMethodType
import javax.validation.constraints.NotBlank

data class AddPaymentMethodDto(
    @field:NotBlank
    val pspToken: String,
    @field:NotBlank
    val paymentMethodType: OrderPaymentMethodType,
)
