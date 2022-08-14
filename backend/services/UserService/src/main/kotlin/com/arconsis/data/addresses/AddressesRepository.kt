package com.arconsis.data.addresses

import com.arconsis.common.toUni
import com.arconsis.data.common.ADDRESS_ID
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
					when (createAddress.isBilling) {
						true -> session.createNamedQuery<AddressEntity>(AddressEntity.UNSET_USER_ADDRESSES_BILLING_ADDRESS_FLAG)
							.setParameter(USER_ID, userId)
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

	fun createBillingAddress(userId: UUID, addressId: UUID): Uni<Address> {
		return sessionFactory.withTransaction { session, _ ->
			session.createNamedQuery<AddressEntity>(AddressEntity.UNSET_USER_ADDRESSES_BILLING_ADDRESS_FLAG)
				.setParameter(USER_ID, userId)
				.executeUpdate()
				.flatMap {
					session.createNamedQuery<AddressEntity>(AddressEntity.SET_IS_BILLING_ADDRESS_FLAG)
						.setParameter(ADDRESS_ID, addressId)
						.executeUpdate()
				}
				.flatMap {
					session.find(AddressEntity::class.java, addressId)
						.map { entity -> entity.toAddress() }
				}
		}
	}

	fun getBillingAddress(userId: UUID): Uni<Address?> {
		return sessionFactory.withTransaction { s, _ ->
			s.createNamedQuery<AddressEntity>(AddressEntity.GET_BILLING_ADDRESS)
				.setParameter(USER_ID, userId)
				.singleResultOrNull
				.map { addressEntity ->
					addressEntity.toAddress()
				}
		}
	}
}