package com.arconsis.domain.baskets

import com.arconsis.common.errors.FailureReason

sealed class BasketsFailureReason(override val message: String, override val errorCode: String) : FailureReason {
	object ProductNotFound : BasketsFailureReason(message = "Provided product not found", errorCode = "PRODUCT_NOT_FOUND")
	object Unknown : BasketsFailureReason(message = "Unknown error", errorCode = "UNKNOWN")
}