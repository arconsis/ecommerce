package com.arconsis.data.addresses

import com.arconsis.domain.addresses.Address

fun AddressEntity.toAddress(): Address {
    return Address(
        addressId = addressId!!,
        name = name,
        address = address,
        houseNumber = houseNumber,
        countryCode = countryCode,
        postalCode = postalCode,
        city = city,
        phone = phone,
        isBilling = isBilling,
        isSelected = isSelected
    )
}