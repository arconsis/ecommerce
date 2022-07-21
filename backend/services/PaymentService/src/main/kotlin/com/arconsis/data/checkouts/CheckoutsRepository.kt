package com.arconsis.data.checkouts

import com.arconsis.domain.orders.OrderItem
import com.stripe.model.checkout.Session
import com.stripe.param.checkout.SessionCreateParams
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CheckoutsRepository(private val checkoutsRemoteStore: CheckoutsRemoteStore) {
	fun createCheckoutSession(orderId: UUID,  orderItems: List<OrderItem>): Session {
		return checkoutsRemoteStore.createCheckoutSession(orderId, orderItems)
	}
}