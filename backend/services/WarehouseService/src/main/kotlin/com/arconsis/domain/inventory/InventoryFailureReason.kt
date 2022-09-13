package com.arconsis.domain.inventory

import com.arconsis.common.errors.FailureReason

sealed class InventoryFailureReason(override val message: String, override val errorCode: String) : FailureReason {
	object ProductNotFound : InventoryFailureReason(message = "Provided product not found", errorCode = "PRODUCT_NOT_FOUND")
	object Unknown : InventoryFailureReason(message = "Unknown error", errorCode = "UNKNOWN")
}