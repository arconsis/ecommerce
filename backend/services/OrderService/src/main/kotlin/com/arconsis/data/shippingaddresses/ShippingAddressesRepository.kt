package com.arconsis.data.shippingaddresses

import com.arconsis.common.body
import com.arconsis.common.bodyAsString
import com.arconsis.common.errors.abort
import com.arconsis.data.shippingaddresses.dto.ShippingAddressValidatorResponseErrorDto
import com.arconsis.domain.shippingaddresses.CreateShippingAddress
import com.arconsis.domain.shippingaddresses.ShippingAddress
import com.arconsis.domain.shippingaddresses.ShippingAddressesFailureReason
import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.hibernate.reactive.mutiny.Mutiny
import org.jboss.logging.Logger
import java.net.URLDecoder
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ShippingAddressesRepository(
	@RestClient private val addressesRemoteStore: ShippingAddressesRemoteStore,
	private val shippingAddressesDataStore: ShippingAddressesDataStore,
	private val logger: Logger
) {
	fun createShippingAddress(createShippingAddress: CreateShippingAddress, basketId: UUID, session: Mutiny.Session): Uni<ShippingAddress> {
		return addressesRemoteStore.validateAddress(createShippingAddress.toShippingAddressValidationBodyRequestDto())
			.flatMap {
				when (it.status) {
					200 -> shippingAddressesDataStore.createShippingAddress(createShippingAddress, basketId, session)
					else               -> {
						logger.error("Address validation operation failed. Response status: ${it.status}, error body: ${it.bodyAsString()}" )
						abort(
							ShippingAddressesFailureReason.InvalidAddress(
							message = URLDecoder.decode(
								it.body<ShippingAddressValidatorResponseErrorDto>(statusCodeRange = 400..499)?.body?.error,
								Charsets.UTF_8
							)
						))
					}
				}
			}
	}
}