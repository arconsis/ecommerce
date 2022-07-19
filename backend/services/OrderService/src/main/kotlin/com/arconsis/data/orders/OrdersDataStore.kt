package com.arconsis.data.orders

import com.arconsis.data.orderitems.OrderItemEntity
import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersDataStore {

	fun updateOrderStatus(orderId: UUID, status: OrderStatus, session: Mutiny.Session): Uni<Order> {
		return session.find(OrderEntity::class.java, orderId)
			.map { orderEntity ->
				orderEntity.status = status
				orderEntity
			}
			.onItem().ifNotNull().transformToUni { orderEntity ->
				session.merge(orderEntity)
			}
			.map { updatedEntity -> updatedEntity.toOrder() }
	}

	fun createOrder(createOrder: CreateOrder, session: Mutiny.Session): Uni<Order> {
		val orderEntity = createOrder.toOrderEntity(OrderStatus.REQUESTED)

		return session.persist(orderEntity)
			.flatMap {
				orderEntity.itemEntities = createOrder.items.map {
					OrderItemEntity(
						productId = it.productId,
						orderId = orderEntity.orderId!!,
						quantity = it.quantity,
						price = it.price,
						currency = it.currency
					)
				}.toMutableList()
				session.persist(orderEntity)
			}
			.map { orderEntity.toOrder() }
	}

	fun getOrder(orderId: UUID, session: Mutiny.Session): Uni<Order> {
		return session.find(OrderEntity::class.java, orderId)
			.map { orderEntity -> orderEntity.toOrder() }
	}
}