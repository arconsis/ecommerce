package com.arconsis.domain.shippingaddresses

import com.arconsis.common.errors.FailureReason

sealed class ShippingAddressesFailureReason(override val message: String, override val errorCode: String) : FailureReason {
	class InvalidAddress (message: String = "Provided address is invalid"): ShippingAddressesFailureReason(message = message, errorCode = "INVALID_ADDRESS")
	object Unknown : ShippingAddressesFailureReason(message = "Unknown error", errorCode = "UNKNOWN")
}