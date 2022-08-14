package com.arconsis.data.products

import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@ApplicationScoped
@RegisterRestClient(configKey = "warehouse")
@Produces(MediaType.APPLICATION_JSON)
interface ProductsRemoteStore {
	@GET
	@Path("products/{productId}")
	fun getProduct(
		@PathParam("productId") productId: UUID
	): Uni<Response>
}
