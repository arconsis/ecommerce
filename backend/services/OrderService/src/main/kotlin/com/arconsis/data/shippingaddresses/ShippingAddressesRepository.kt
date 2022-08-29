package com.arconsis.data.shippingaddresses

import com.arconsis.domain.shippingaddresses.CreateShippingAddress
import com.arconsis.domain.shippingaddresses.ShippingAddress
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShippingAddressesRepository(private val shippingAddressesDataStore: ShippingAddressesDataStore) {
	fun createShippingAddress(createShippingAddress: CreateShippingAddress, basketId: UUID, session: Mutiny.Session): Uni<ShippingAddress> {
		return shippingAddressesDataStore.createShippingAddress(createShippingAddress, basketId, session)
	}
}