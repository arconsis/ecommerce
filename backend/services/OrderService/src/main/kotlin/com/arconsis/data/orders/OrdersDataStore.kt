package com.arconsis.data.orders

import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.asUni
import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersDataStore(private val sessionFactory: Mutiny.SessionFactory,) {

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
			.map { orderEntity.toOrder() }
	}

	fun getOrder(orderId: UUID, session: Mutiny.Session): Uni<Order> {
		return session.find(OrderEntity::class.java, orderId)
			.map { orderEntity -> orderEntity.toOrder() }
	}


	suspend fun <T> withTransaction(block: Mutiny.Session.(Mutiny.Transaction) -> Uni<T>): T = withRetryingTransaction { transaction ->
		block(transaction)
	}

	suspend fun <T> coWithTransaction(block: suspend Mutiny.Session.(Mutiny.Transaction) -> T): T = coroutineScope {
		withRetryingTransaction { transaction ->
			async {
				block(transaction)
			}.asUni()
		}
	}

	private suspend fun <T> withRetryingTransaction(block: Mutiny.Session.(Mutiny.Transaction) -> Uni<T>): T = withRetry {
		withTransaction { session, transaction -> session.block(transaction) }
	}

	private suspend fun <T> withRetry(block: suspend Mutiny.SessionFactory.() -> Uni<T>): T =
		sessionFactory.block()
			.onFailure()
			.retry()
			.atMost(0)
			.awaitSuspending()
}