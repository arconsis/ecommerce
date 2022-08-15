package com.arconsis.data.addresses

import com.arconsis.domain.addresses.Address
import com.arconsis.domain.addresses.CreateAddress
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class AddressesRepository(private val addressesDataStore: AddressesDataStore) {
	fun createShippingAddress(createAddress: CreateAddress, basketId: UUID, session: Mutiny.Session): Uni<Address> {
		return addressesDataStore.createShippingAddress(createAddress, basketId, session)
	}
}