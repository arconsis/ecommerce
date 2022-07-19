package com.arconsis.data.orderitems

import com.arconsis.domain.orderitems.CreateOrderItem
import com.arconsis.domain.orderitems.OrderItem
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import javax.enterprise.context.ApplicationScoped

//@ApplicationScoped
//class OrderItemsRepository(val orderItemsDataStore: OrderItemsDataStore) {
//	fun createOrderItem(newProduct: CreateOrderItem, session: Mutiny.Session): Uni<OrderItem> {
//		return orderItemsDataStore.createOrderItem(newProduct, session)
//	}
//}