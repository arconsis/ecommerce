package com.arconsis.data.shippingaddresses

import com.arconsis.data.shippingaddresses.dto.ShippingAddressValidationBodyRequestDto
import com.arconsis.domain.shippingaddresses.CreateShippingAddress
import com.arconsis.domain.shippingaddresses.ShippingAddress

fun ShippingAddressEntity.toAddress(): ShippingAddress {
    return ShippingAddress(
        addressId = addressId!!,
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
}

fun CreateShippingAddress.toShippingAddressValidationBodyRequestDto() = ShippingAddressValidationBodyRequestDto(
    firstName = firstName,
    lastName = lastName,
    address = address,
    houseNumber = houseNumber,
    countryCode = countryCode,
    postalCode = postalCode,
    city = city,
    phone = phone,
    state = state
)