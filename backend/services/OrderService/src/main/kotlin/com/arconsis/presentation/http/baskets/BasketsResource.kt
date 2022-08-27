package com.arconsis.presentation.http.baskets

import com.arconsis.domain.baskets.Basket
import com.arconsis.domain.baskets.BasketsService
import com.arconsis.presentation.http.baskets.dto.AddPaymentMethodDto
import com.arconsis.presentation.http.baskets.dto.AddShippingProviderDto
import com.arconsis.presentation.http.baskets.dto.CreateAddressDto
import com.arconsis.presentation.http.baskets.dto.CreateBasketDto
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.UriInfo

@ApplicationScoped
@Path("/baskets")
class BasketsResource(val basketsService: BasketsService) {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	fun createBasket(newBasketRequest: CreateBasketDto, uriInfo: UriInfo): Uni<Basket> {
		return basketsService.createBasket(newBasketRequest)
	}

	@GET
	@Path("/{basketId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	fun getOrder(@PathParam("basketId") basketId: UUID): Uni<Basket?> {
		return basketsService.getBasket(basketId)
	}

	@POST
	@Path("/{basketId}/shipping_address")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	fun createBasketShippingAddress(@PathParam("basketId") basketId: UUID, address: CreateAddressDto, uriInfo: UriInfo): Uni<Basket> {
		return basketsService.createBasketShippingAddress(basketId, address)
	}

	@POST
	@Path("/{basketId}/payment_method")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	fun addBasketPaymentMethod(@PathParam("basketId") basketId: UUID, newPaymentMethod: AddPaymentMethodDto, uriInfo: UriInfo): Uni<Basket> {
		return basketsService.updateBasketPaymentMethod(basketId, newPaymentMethod)
	}

	@POST
	@Path("/{basketId}/shipping_provider")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	fun updateBasketShippingProvider(@PathParam("basketId") basketId: UUID, newShippingProvider: AddShippingProviderDto, uriInfo: UriInfo): Uni<Basket> {
		return basketsService.updateBasketShippingProvider(basketId, newShippingProvider)
	}
}
