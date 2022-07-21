package com.arconsis.data.orders

import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersRepository(private val ordersDataStore: OrdersDataStore) {

    fun updateOrderStatus(orderId: UUID, status: OrderStatus, session: Mutiny.Session): Uni<Order> {
        return ordersDataStore.updateOrderStatus(orderId, status, session)
    }

    fun updateOrderCheckout(
        orderId: UUID,
        status: OrderStatus,
        checkoutSessionId: String,
        checkoutUrl: String,
        session: Mutiny.Session
    ): Uni<Order> {
        return ordersDataStore.updateOrderCheckout(orderId, status, checkoutSessionId, checkoutUrl, session)
    }

    fun createOrder(createOrder: CreateOrder, session: Mutiny.Session): Uni<Order> {
        return ordersDataStore.createOrder(createOrder, session)
    }

    fun getOrder(orderId: UUID, session: Mutiny.Session): Uni<Order> {
        return ordersDataStore.getOrder(orderId, session)
    }
}