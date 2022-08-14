package com.arconsis.domain.addresses

import com.arconsis.data.addresses.AddressesRepository
import com.arconsis.presentation.http.dto.CreateAddressDto
import com.arconsis.presentation.http.dto.toCreateAddress
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class AddressesService(private val addressesRepository: AddressesRepository) {

    fun getAddresses(userId: UUID): Uni<List<Address>> {
        return addressesRepository.getAddresses(userId)
    }

    fun createAddress(createAddress: CreateAddressDto, userId: UUID): Uni<Address> {
        val newAddress = createAddress.toCreateAddress(isSelected = true)
        return addressesRepository.createAddress(newAddress, userId)
    }

    fun getBillingAddress(userId: UUID): Uni<Address?> {
        return addressesRepository.getBillingAddress(userId)
    }

    fun createBillingAddress(userId: UUID, addressId: UUID): Uni<Address> {
        return addressesRepository.createBillingAddress(userId, addressId)
    }
}