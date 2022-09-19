package com.arconsis.presentation.http.dto

import com.arconsis.domain.addresses.CreateAddress
import com.arconsis.domain.addresses.SupportedCountryCode
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreateAddressDto(
	@field:NotBlank
    val firstName: String,
	@field:NotBlank
	val lastName: String,
	@field:NotBlank
    val address: String,
	@field:NotBlank
    val houseNumber: String,
	@field:NotNull
    val countryCode: SupportedCountryCode,
	@field:NotBlank
    val postalCode: String,
	@field:NotBlank
    val city: String,
	@field:NotBlank
    val phone: String,
	@field:NotBlank
	val state: String,
)

fun CreateAddressDto.toCreateAddress(isSelected: Boolean) = CreateAddress(
    firstName = firstName,
	lastName = lastName,
    address = address,
    houseNumber = houseNumber,
    countryCode = countryCode,
    postalCode = postalCode,
    city = city,
    phone = phone,
	state = state,
    isSelected = isSelected
)