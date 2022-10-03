package com.arconsis.data.orders

import com.arconsis.common.asPair
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
import javax.persistence.criteria.Root

@ApplicationScoped
class OrdersDataStore(private val sessionFactory: Mutiny.SessionFactory) {
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

	fun listOrders(userId: UUID, search: String?, limit: Int, offset: Int): Uni<Pair<List<Order>, Int>> {
		return sessionFactory.withTransaction { session, _ ->
			Uni.combine().all().unis(
				listOrdersByQuery(userId, search, limit, offset, session),
				countOrders(userId, search, session),
			).asPair()
				.map { (list, total) ->
					list.map { entity -> entity.toOrder() } to total
				}
		}
	}

	private fun getSearchParamsToListUsers(userId: UUID, search: String?): List<Pair<String, *>> {
		val defaultParams: MutableList<Pair<String, *>> = mutableListOf(Pair("userId", userId))
		val searchParams: MutableList<Pair<String, *>> = search?.split(";")
			?.map { searchParam ->
				val keyValue = searchParam.split(":")
				Pair(keyValue[0], keyValue[1])
			}?.toMutableList()
			?: mutableListOf()
		return (defaultParams + searchParams)
	}

	private fun listOrdersByQuery(userId: UUID, search: String?, limit: Int, offset: Int, session: Mutiny.Session): Uni<List<OrderEntity>> {
		val cb = sessionFactory.criteriaBuilder
		val criteria = cb.createQuery(OrderEntity::class.java)
		val root = criteria.from(OrderEntity::class.java)
		val searchParams = getSearchParamsToListUsers(userId, search)
		searchParams.forEach { (key, value) ->
			criteria.where(cb.equal(root.get<String>(key), "$value"))
		}
		val query = session.createQuery(criteria)
		query.firstResult = offset
		query.maxResults = limit
		return query.resultList
	}

	private fun countOrders(userId: UUID, search: String?, session: Mutiny.Session): Uni<Int> {
		val cb = sessionFactory.criteriaBuilder
		val criteria = cb.createQuery(Long::class.java)
		val root: Root<OrderEntity> = criteria.from(OrderEntity::class.java)
		val searchParams = getSearchParamsToListUsers(userId, search)
		searchParams.forEach { (key, value) ->
			criteria.where(cb.equal(root.get<String>(key), "$value"))
		}
		criteria.select(cb.count(root))
		return session.createQuery(criteria)
			.resultList
			.map {
				it.firstOrNull()?.toInt() ?: 0
			}
	}
}