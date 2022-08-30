package com.arconsis.data.shipments

import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@ApplicationScoped
@RegisterRestClient(configKey = "shippo")
@Produces(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(ShipmentLabelsRemoteStoreHeadersFactory::class)
interface ShipmentLabelsRemoteStore {
	@POST
	@Path("transactions")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	fun createShipmentLabel(
		@FormParam("rate") providerId: String,
		@FormParam("label_file_type") labelFileType: String,
		@FormParam("async") async: String,
	): Uni<Response>
}