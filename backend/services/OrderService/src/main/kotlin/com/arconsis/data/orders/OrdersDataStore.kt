package com.arconsis.data.orders

import com.arconsis.data.outboxevents.toOutboxEvent
import com.arconsis.data.processedevents.ProcessedEventEntity
import com.arconsis.data.processedevents.toProcessedEvent
import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersDataStore : PanacheRepository<OrderEntity> {

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
				print(it.message)
				false
			}.recoverWithUni(getOrder(orderId))
	}
}