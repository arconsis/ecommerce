package com.arconsis.domain.shippingproviders

import com.arconsis.common.errors.FailureReason

sealed class ShippingProvidersFailureReason(override val message: String, override val errorCode: String) : FailureReason {
	object ShippingProviderNotFound : ShippingProvidersFailureReason(message = "Shipping provider not found", errorCode = "SHIPPING_PROVIDER_NOT_FOUND")
	object ShippingProviderTokenNotFound : ShippingProvidersFailureReason(message = "Shipping provider token not found", errorCode = "SHIPPING_PROVIDER_TOKEN_NOT_FOUND")
	object Unknown : ShippingProvidersFailureReason(message = "Unknown error", errorCode = "UNKNOWN")
}