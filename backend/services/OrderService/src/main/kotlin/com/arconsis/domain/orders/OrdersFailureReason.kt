package com.arconsis.domain.orders

import com.arconsis.common.errors.FailureReason

sealed class OrdersFailureReason(override val message: String, override val errorCode: String) : FailureReason {
	object BasketNotFound : OrdersFailureReason(message = "Provided basket not found", errorCode = "BASKET_NOT_FOUND")
	object BasketNotOrderable : OrdersFailureReason(message = "Provided basket is not orderable", errorCode = "BASKET_NOT_ORDERABLE")
	object Unknown : OrdersFailureReason(message = "Unknown error", errorCode = "UNKNOWN")
}