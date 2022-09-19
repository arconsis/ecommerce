package com.arconsis.domain.addresses

import com.arconsis.common.errors.FailureReason

sealed class AddressesFailureReason(override val message: String, override val errorCode: String) : FailureReason {
	class InvalidAddress (message: String = "Provided address is invalid"): AddressesFailureReason(message = message, errorCode = "INVALID_ADDRESS")
	object Unknown : AddressesFailureReason(message = "Unknown error", errorCode = "UNKNOWN")
}