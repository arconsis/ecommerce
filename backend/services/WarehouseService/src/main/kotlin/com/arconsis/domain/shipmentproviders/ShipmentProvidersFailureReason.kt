package com.arconsis.domain.shipmentproviders

import com.arconsis.common.errors.FailureReason

sealed class ShipmentProvidersFailureReason(override val message: String, override val errorCode: String) : FailureReason {
	object ShipmentProviderNotFound : ShipmentProvidersFailureReason(message = "Shipment provider not found", errorCode = "SHIPMENT_PROVIDER_NOT FOUND")
	object ShipmentProviderTokenNotFound : ShipmentProvidersFailureReason(message = "Shipment provider token not found", errorCode = "SHIPMENT_PROVIDER_TOKEN_NOT FOUND")
	object Unknown : ShipmentProvidersFailureReason(message = "Unknown error", errorCode = "UNKNOWN")
}