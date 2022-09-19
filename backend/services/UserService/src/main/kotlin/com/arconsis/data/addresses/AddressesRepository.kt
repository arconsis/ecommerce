package com.arconsis.data.addresses

import com.arconsis.common.body
import com.arconsis.common.bodyAsString
import com.arconsis.common.errors.abort
import com.arconsis.data.addresses.dto.AddressValidatorResponseErrorDto
import com.arconsis.data.addresses.dto.toAddressValidationBodyRequestDto
import com.arconsis.domain.addresses.Address
import com.arconsis.domain.addresses.AddressesFailureReason
import com.arconsis.domain.addresses.CreateAddress
import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import java.net.URLDecoder
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class AddressesRepository(
	private val addressesDataStore: AddressesDataStore,
	@RestClient private val addressesRemoteStore: AddressesRemoteStore,
	private val logger: Logger
) {
	fun createAddress(createAddress: CreateAddress, userId: UUID): Uni<Address> {
		return addressesDataStore.createAddress(createAddress, userId)
	}

	fun getAddresses(userId: UUID): Uni<List<Address>> {
		return addressesDataStore.getAddresses(userId)
	}

	fun validateAddress(createAddress: CreateAddress): Uni<Boolean> {
		return addressesRemoteStore.validateAddress(createAddress.toAddressValidationBodyRequestDto())
			.map {
				when (it.status) {
					200 -> true
					else               -> {
						logger.error("Address validation operation failed. Response status: ${it.status}, error body: ${it.bodyAsString()}" )
						abort(AddressesFailureReason.InvalidAddress(
							message = URLDecoder.decode(
								it.body<AddressValidatorResponseErrorDto>(statusCodeRange = 400..499)?.body?.error,
								Charsets.UTF_8
							)
						))
					}
				}
			}
	}
}