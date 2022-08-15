package com.arconsis.data.addresses

import com.arconsis.common.toUni
import com.arconsis.domain.addresses.Address
import com.arconsis.domain.addresses.CreateAddress
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class AddressesDataStore {
	fun createShippingAddress(createAddress: CreateAddress, basketId: UUID, session: Mutiny.Session): Uni<Address> {
		return session.createNamedQuery<AddressEntity>(AddressEntity.UNSET_BASKET_ADDRESSES_IS_SELECTED_ADDRESS_FLAG)
			.setParameter("basketId", basketId)
			.executeUpdate()
			.flatMap {
				when (createAddress.isBilling) {
					true -> session.createNamedQuery<AddressEntity>(AddressEntity.UNSET_BASKET_ADDRESSES_BILLING_ADDRESS_FLAG)
						.setParameter("basketId", basketId)
						.executeUpdate()
					else -> it.toUni()
				}
			}.flatMap {
				val addressEntity = AddressEntity(
					name = createAddress.name,
					address = createAddress.address,
					houseNumber = createAddress.houseNumber,
					countryCode = createAddress.countryCode,
					postalCode = createAddress.postalCode,
					city = createAddress.city,
					phone = createAddress.phone,
					isBilling = createAddress.isBilling,
					isSelected = createAddress.isSelected,
					basketId = basketId,
				)
				session.persist(addressEntity)
					.map { addressEntity.toAddress() }
			}
	}
}