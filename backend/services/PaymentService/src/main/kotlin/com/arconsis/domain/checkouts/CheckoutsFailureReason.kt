package com.arconsis.domain.checkouts

import com.arconsis.common.errors.FailureReason

sealed class CheckoutsFailureReason(override val message: String, override val errorCode: String) : FailureReason {
	object CheckoutNotFound : CheckoutsFailureReason(message = "Provided checkout not found", errorCode = "CHECKOUT_NOT_FOUND")
	object Unknown : CheckoutsFailureReason(message = "Unknown error", errorCode = "UNKNOWN")
}