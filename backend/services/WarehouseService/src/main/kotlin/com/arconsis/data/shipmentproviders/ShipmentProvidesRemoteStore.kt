package com.arconsis.data.shipmentproviders

import com.arconsis.data.shipmentproviders.dto.ListShipmentProvidersDto
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@ApplicationScoped
@RegisterRestClient(configKey = "shippo")
@Produces(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(ShipmentProvidersRemoteStoreHeadersFactory::class)
interface ShipmentProvidesRemoteStore {
	@POST
	@Path("shipments")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	suspend fun listShipmentProviders(
		listShipmentProviders: ListShipmentProvidersDto,
	): Response
}