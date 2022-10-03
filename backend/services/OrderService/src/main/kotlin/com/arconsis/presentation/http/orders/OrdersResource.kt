package com.arconsis.presentation.http.orders

import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrdersService
import com.arconsis.presentation.http.orders.dto.request.CreateOrderDto
import com.arconsis.presentation.http.orders.dto.response.ListOrdersPaginationResponseDto
import com.arconsis.presentation.http.orders.dto.response.ListOrdersResponseDto
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.UriInfo

@ApplicationScoped
@Path("/orders")
class OrdersResource(
	private val ordersService: OrdersService,
) {

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	fun createOrder(createOrder: CreateOrderDto, uriInfo: UriInfo): Uni<Order> {
		return ordersService.createOrder(createOrder)
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	// TODO: we have to get userId / sub from token
	fun listUserOrders(
		@QueryParam("userId") userId: UUID,
		@QueryParam("offset") offset: Int?,
		@QueryParam("limit") limit: Int?,
		@QueryParam("search") search: String?
	): Uni<ListOrdersResponseDto> {
        val (queryOffset, queryLimit) = getPaginationSize(offset, limit)
		return ordersService.listOrders(userId, search, queryLimit, queryOffset)
            .map { (orders, total) ->
                ListOrdersResponseDto(
                    data = orders,
                    pagination = ListOrdersPaginationResponseDto(
                        offset = queryOffset,
                        limit = queryLimit,
                        total = total
                    )
                )
            }
	}

	@GET
	@Path("/{orderId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	fun getOrder(@PathParam("orderId") orderId: UUID): Uni<Order> {
		return ordersService.getOrder(orderId)
	}

    private fun getPaginationSize(offset: Int?, limit: Int?): Pair<Int, Int> {
        val queryOffset = offset ?: DEFAULT_OFFSET
        val queryLimit = when (limit) {
            null -> DEFAULT_LIMIT
            in MIN_LIMIT..MAX_LIMIT -> limit
            else -> DEFAULT_LIMIT
        }
        return queryOffset to queryLimit
    }

    companion object {
        private const val MAX_LIMIT = 100
        private const val MIN_LIMIT = 1
        private const val DEFAULT_LIMIT = 33
        private const val DEFAULT_OFFSET = 0
    }
}