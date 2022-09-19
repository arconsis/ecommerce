package com.arconsis.data.shippingaddresses

import com.arconsis.data.shippingaddresses.dto.ShippingAddressValidationBodyRequestDto
import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@ApplicationScoped
@RegisterRestClient(configKey = "address-validation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface ShippingAddressesRemoteStore {
	@POST
	@Path("address/validation")
	fun validateAddress(
		address: ShippingAddressValidationBodyRequestDto
	): Uni<Response>
}