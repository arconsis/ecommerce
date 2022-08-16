package com.arconsis.data.orders

import com.arconsis.common.errors.abort
import com.arconsis.data.baskets.BasketEntity
import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.orders.OrdersFailureReason
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersDataStore {
	fun updateOrderCheckout(
		orderId: UUID,
		status: OrderStatus,
		checkoutSessionId: String,
		checkoutUrl: String,
		session: Mutiny.Session
	): Uni<Order> {
		return session.find(OrderEntity::class.java, orderId)
			.map { orderEntity ->
				orderEntity.status = status
				orderEntity.checkoutUrl = checkoutUrl
				orderEntity.checkoutSessionId = checkoutSessionId
				orderEntity
			}
			.onItem().ifNotNull().transformToUni { orderEntity ->
				session.merge(orderEntity)
			}
			.map { updatedEntity -> updatedEntity.toOrder() }
	}

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
		val basket = session.getReference(BasketEntity::class.java, createOrder.basketId) ?: abort(OrdersFailureReason.BasketNotFound)
		val orderEntity = createOrder.toOrderEntity(OrderStatus.REQUESTED, basket)
		return session.persist(orderEntity)
			.map { orderEntity.toOrder() }
	}

	fun getOrder(orderId: UUID, session: Mutiny.Session): Uni<Order> {
		return session.find(OrderEntity::class.java, orderId)
			.map { orderEntity -> orderEntity.toOrder() }
	}
}