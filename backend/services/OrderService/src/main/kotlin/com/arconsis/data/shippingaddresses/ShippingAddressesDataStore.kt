package com.arconsis.data.shippingaddresses

import com.arconsis.common.toUni
import com.arconsis.domain.shippingaddresses.CreateShippingAddress
import com.arconsis.domain.shippingaddresses.ShippingAddress
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShippingAddressesDataStore {
	fun createShippingAddress(createShippingAddress: CreateShippingAddress, basketId: UUID, session: Mutiny.Session): Uni<ShippingAddress> {
		return session.createNamedQuery<ShippingAddressEntity>(ShippingAddressEntity.UNSET_BASKET_ADDRESSES_IS_SELECTED_ADDRESS_FLAG)
			.setParameter("basketId", basketId)
			.executeUpdate()
			.flatMap {
				when (createShippingAddress.isBilling) {
					true -> session.createNamedQuery<ShippingAddressEntity>(ShippingAddressEntity.UNSET_BASKET_ADDRESSES_BILLING_ADDRESS_FLAG)
						.setParameter("basketId", basketId)
						.executeUpdate()
					else -> it.toUni()
				}
			}.flatMap {
				val shippingAddressEntity = ShippingAddressEntity(
					firstName = createShippingAddress.firstName,
					lastName = createShippingAddress.lastName,
					address = createShippingAddress.address,
					houseNumber = createShippingAddress.houseNumber,
					countryCode = createShippingAddress.countryCode,
					postalCode = createShippingAddress.postalCode,
					city = createShippingAddress.city,
					phone = createShippingAddress.phone,
					isBilling = createShippingAddress.isBilling,
					isSelected = createShippingAddress.isSelected,
					state = createShippingAddress.state,
					basketId = basketId,
				)
				session.persist(shippingAddressEntity)
					.map { shippingAddressEntity.toAddress() }
			}
	}
}