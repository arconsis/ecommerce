package com.arconsis.data.addresses

import com.arconsis.data.common.USER_ID
import com.arconsis.domain.addresses.Address
import com.arconsis.domain.addresses.CreateAddress
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class AddressesRepository(private val sessionFactory: Mutiny.SessionFactory) {
	fun createAddress(createAddress: CreateAddress, userId: UUID): Uni<Address> {
		return sessionFactory.withTransaction { session, _ ->
			session.createNamedQuery<AddressEntity>(AddressEntity.UNSET_USER_ADDRESSES_IS_SELECTED_ADDRESS_FLAG)
				.setParameter(USER_ID, userId)
				.executeUpdate()
				.flatMap {
					val addressEntity = AddressEntity(
						name = createAddress.name,
						address = createAddress.address,
						houseNumber = createAddress.houseNumber,
						countryCode = createAddress.countryCode,
						postalCode = createAddress.postalCode,
						city = createAddress.city,
						phone = createAddress.phone,
						isSelected = createAddress.isSelected,
						userId = userId,
					)
					session.persist(addressEntity)
						.map { addressEntity.toAddress() }
				}

		}
	}

	fun getAddresses(userId: UUID): Uni<List<Address>> {
		return sessionFactory.withTransaction { s, _ ->
			s.createNamedQuery<AddressEntity>(AddressEntity.LIST_USER_ADDRESSES)
				.setParameter("user_id", userId)
				.resultList
				.map {
					it.map { addressEntity -> addressEntity.toAddress() }
				}
		}
	}
}