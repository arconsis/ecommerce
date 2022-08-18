package com.arconsis.domain.orders

import com.arconsis.common.asPair
import com.arconsis.common.errors.abort
import com.arconsis.common.toUni
import com.arconsis.data.baskets.BasketsRepository
import com.arconsis.data.orders.OrdersRepository
import com.arconsis.data.outboxevents.OutboxEventsRepository
import com.arconsis.domain.baskets.toOrderItem
import com.arconsis.presentation.http.orders.dto.CreateOrderDto
import com.arconsis.presentation.http.orders.dto.toCreateOrder
import com.fasterxml.jackson.databind.ObjectMapper
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
	private val basketsRepository: BasketsRepository,
	private val ordersRepository: OrdersRepository,
	private val outboxEventsRepository: OutboxEventsRepository,
	private val sessionFactory: Mutiny.SessionFactory,
	private val objectMapper: ObjectMapper,
) {
	fun createOrder(createOrder: CreateOrderDto): Uni<Order> {
		return sessionFactory.withTransaction { session, _ ->
			basketsRepository.getBasket(createOrder.basketId, session)
				.flatMap { basket ->
					if (basket == null) {
						abort(OrdersFailureReason.BasketNotFound)
					}
					if (!basket.isOrderable) {
						abort(OrdersFailureReason.BasketNotOrderable)
					}
					Uni.combine().all().unis(
						ordersRepository.createOrder(createOrder.toCreateOrder(basket.prices.totalPrice, basket.prices.priceBeforeTax, basket.prices.tax, basket.prices.currency, basket.paymentMethod!!.pspToken, basket.paymentMethod.paymentMethodType), session),
						basket.toUni(),
					).asPair()
				}
				.flatMap { (order, basket) ->
					val enrichedOrder = order.copy(items = basket.items.map { it.toOrderItem(order.orderId) })
					val createOutboxEvent = enrichedOrder.toCreateOutboxEvent(objectMapper)
					outboxEventsRepository.createEvent(createOutboxEvent, session).map {
						enrichedOrder
					}
				}
		}
	}

	fun getOrder(orderId: UUID): Uni<Order> {
		return sessionFactory.withTransaction { session, _ ->
			ordersRepository.getOrder(orderId, session)
		}
	}
}