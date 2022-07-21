package com.arconsis.domain.payments

import com.arconsis.domain.checkout.Checkout
import java.math.BigDecimal
import java.util.*

data class Payment(
    val paymentId: UUID,
    val transactionId: UUID?,
    val orderId: UUID,
    val userId: UUID,
    val amount: BigDecimal,
    val currency: String,
    val status: PaymentStatus,
    val checkout: Checkout
)

enum class PaymentStatus {
    IN_PROGRESS,
    SUCCEED,
    FAILED
}