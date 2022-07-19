package com.arconsis.data.orderitems

import com.arconsis.domain.orderitems.CreateOrderItem
import com.arconsis.domain.orderitems.OrderItem
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny.Session
import javax.enterprise.context.ApplicationScoped

//@ApplicationScoped
//class OrderItemsDataStore {
//	fun createOrderItem(newProduct: CreateOrderItem, session: Session): Uni<OrderItem> {
//		val entity = newProduct.toProductEntity()
//		return session.persist(entity)
//			.map { entity.toProduct() }
//	}
//}
