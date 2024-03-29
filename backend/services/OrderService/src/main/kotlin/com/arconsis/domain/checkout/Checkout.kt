package com.arconsis.domain.checkout

import com.arconsis.domain.orders.SupportedCurrencies
import java.math.BigDecimal
import java.util.*

data class Checkout(
	val checkoutId: UUID,
	val orderId: UUID,
	val userId: UUID,
	val amount: BigDecimal,
	val currency: SupportedCurrencies,
	val paymentStatus: CheckoutStatus,
	val paymentErrorMessage: String?,
	val paymentErrorCode: String?,
	// psp ref
	val pspToken: String
)

enum class CheckoutStatus {
	PAYMENT_IN_PROGRESS,
	PAYMENT_SUCCEED,
	PAYMENT_FAILED,
}
