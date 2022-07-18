package com.arconsis.data.orders

import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import io.smallrye.mutiny.Uni
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersRepository(private val ordersDataStore: OrdersDataStore) {

    fun updateOrderStatus(orderId: UUID, status: OrderStatus): Uni<Order> {
        return ordersDataStore.updateOrderStatus(orderId, status)
    }

    fun createOrder(createOrder: CreateOrder): Uni<Order> {
        return ordersDataStore.createOrder(createOrder)
    }

    fun getOrder(orderId: UUID): Uni<Order> {
        return ordersDataStore.getOrder(orderId)
    }
}