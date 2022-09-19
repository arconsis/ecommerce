package com.arconsis.data.addresses

import com.arconsis.domain.addresses.Address

fun AddressEntity.toAddress(): Address {
    return Address(
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
        isSelected = isSelected
    )
}