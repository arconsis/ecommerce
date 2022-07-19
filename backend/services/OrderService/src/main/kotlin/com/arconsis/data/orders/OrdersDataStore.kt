package com.arconsis.data.orders

import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.Uni
import org.jboss.logging.Logger
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersDataStore(val logger: Logger) : PanacheRepository<OrderEntity> {

	fun createOrder(createOrder: CreateOrder): Uni<Order> {
		val orderEntity = createOrder.toOrderEntity(OrderStatus.REQUESTED)
		return persist(orderEntity)
			.map {
				orderEntity.toOrder()
			}
	}

	fun getOrder(orderId: UUID): Uni<Order> {
		return find("orderId", orderId)
			.firstResult<OrderEntity?>()
			.map {
				it.toOrder()
			}
	}

	fun updateOrderStatus(orderId: UUID, status: OrderStatus): Uni<Order> {
		val params: MutableMap<String, Any> = HashMap()
		params["status"] = status
		params["orderId"] = orderId
		return update("update orders o set o.status = :status where o.orderId = :orderId", params)
			// TODO: Check if we have concurrency issues
			.flatMap { getOrder(orderId) }
			.onFailure {
				logger.info("Error with updateOrderStatus")
				false
			}.recoverWithUni(getOrder(orderId))
	}
}