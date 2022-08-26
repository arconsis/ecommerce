package com.arconsis.data.shipments

import com.arconsis.data.shipments.dto.ListShipmentProvidersDto
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@ApplicationScoped
@RegisterRestClient(configKey = "shippo")
@Produces(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(ShipmentsRemoteStoreHeadersFactory::class)
interface ShipmentsRemoteStore {
	@POST
	@Path("shipments")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	suspend fun listShipmentProviders(
		listShipmentProviders: ListShipmentProvidersDto,
	): Response

	@POST
	@Path("transactions")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	suspend fun createShipment(
		@FormParam("rate") providerId: String,
		@FormParam("label_file_type") labelFileType: String = "PDF",
		@FormParam("async") async: Boolean = false,
	): Response
}