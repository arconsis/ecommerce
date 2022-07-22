package com.arconsis.data.checkouts

import com.arconsis.domain.checkouts.Checkout
import com.arconsis.domain.checkouts.CheckoutStatus
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.toCreateCheckout
import io.smallrye.mutiny.Uni
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CheckoutsRepository(
	private val checkoutsRemoteStore: CheckoutsRemoteStore,
	private val checkoutsDataStore: CheckoutsDataStore
) {
	fun createCheckout(order: Order, session: Mutiny.Session): Uni<Checkout> {
		val pspSession = checkoutsRemoteStore.createCheckoutSession(order.orderId, order.items)
		val newCheckout = order.toCreateCheckout(checkoutUrl = pspSession.url, checkoutSessionId = pspSession.id, status = CheckoutStatus.PAYMENT_IN_PROGRESS)
		return checkoutsDataStore.createCheckout(newCheckout, session)
	}

	fun updateCheckoutStatus(checkoutId: UUID, status: CheckoutStatus, session: Mutiny.Session): Uni<Checkout> {
		return checkoutsDataStore.updateCheckoutStatus(checkoutId, status, session)
	}

	fun getCheckoutByOrderId(orderId: UUID, session: Mutiny.Session): Uni<Checkout?> {
		return checkoutsDataStore.getCheckoutByOrderId(orderId, session)
	}
}