package com.arconsis.data.shipmentproviders.dto

import com.arconsis.domain.orders.SupportedCurrencies
import com.fasterxml.jackson.annotation.JsonProperty

data class ServiceGroupResponseDto(
	@JsonProperty("description") val description: String,
	@JsonProperty("flat_rate") val price: String,
	@JsonProperty("flat_rate_currency") val currency: SupportedCurrencies,
	@JsonProperty("name") val name: String,
	@JsonProperty("type") val type: String,
	@JsonProperty("service_levels") val serviceLevels: List<ServiceGroupLevelsResponseDto>,
	@JsonProperty("is_active") val isActive: Boolean,
)

data class ServiceGroupLevelsResponseDto(
	@JsonProperty("account_object_id") val carrierAccount: String,
	@JsonProperty("service_level_token") val token: String,
)