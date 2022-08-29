package com.arconsis.presentation.http.baskets.dto

import com.arconsis.domain.shippingaddresses.CreateShippingAddress
import com.arconsis.domain.shippingaddresses.SupportedCountryCode
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CreateShippingAddressDto(
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

fun CreateShippingAddressDto.toCreateShippingAddress(isSelected: Boolean, isBilling: Boolean) = CreateShippingAddress(
    firstName = firstName,
    lastName = lastName,
    address = address,
    houseNumber = houseNumber,
    countryCode = countryCode,
    postalCode = postalCode,
    city = city,
    phone = phone,
    state = state,
    isBilling = isBilling,
    isSelected = isSelected
)