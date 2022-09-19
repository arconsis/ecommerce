package com.arconsis.data.addresses

import com.arconsis.data.addresses.dto.AddressValidationBodyRequestDto
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
interface AddressesRemoteStore {
	@POST
	@Path("address/validation")
	fun validateAddress(
		address: AddressValidationBodyRequestDto
	): Uni<Response>
}