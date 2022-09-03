package com.arconsis.data.shipmentlabels

import com.arconsis.data.shipmentlabels.dto.request.CreateShipmentLabelDto
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
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	fun createShipmentLabel(
		createShipmentLabelDto: CreateShipmentLabelDto,
	): Uni<Response>
}