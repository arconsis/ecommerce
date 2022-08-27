package com.arconsis.domain.shipments

import com.arconsis.common.errors.FailureReason

sealed class ShipmentsFailureReason(override val message: String, override val errorCode: String) : FailureReason {
	object Unknown : ShipmentsFailureReason(message = "Unknown error", errorCode = "UNKNOWN")
}