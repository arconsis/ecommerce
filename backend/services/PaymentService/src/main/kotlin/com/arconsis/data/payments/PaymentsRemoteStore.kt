package com.arconsis.data.payments

import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@ApplicationScoped
@RegisterRestClient(configKey = "stripe")
@Produces(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(PaymentsRemoteStoreHeadersFactory::class)
interface PaymentsRemoteStore {
	@POST
	@Path("charges")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	fun charge(
		@FormParam("amount") amount: String,
		@FormParam("currency") currency: String,
		@FormParam("source") source: String,
		@FormParam("description") description: String,
		@FormParam("metadata[order_id]") orderId: String
	): Uni<Response>
}