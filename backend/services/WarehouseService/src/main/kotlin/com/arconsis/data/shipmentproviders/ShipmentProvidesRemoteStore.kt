package com.arconsis.data.shipmentproviders

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
@RegisterClientHeaders(ShipmentProvidersRemoteStoreHeadersFactory::class)
interface ShipmentProvidesRemoteStore {
	@GET
	@Path("rates/{providerId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	fun getShipmentProviderRate(@PathParam("providerId") providerId: String): Uni<Response>

	@GET
	@Path("service-groups")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	fun listServiceGroups(): Uni<Response>

	@GET
	@Path("carrier_accounts/{carrierAccountId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	fun getCarrierAccount(
		@PathParam("carrierAccountId") carrierAccountId: String,
		@QueryParam("service_levels") serviceLevels: Int
	): Uni<Response>
}