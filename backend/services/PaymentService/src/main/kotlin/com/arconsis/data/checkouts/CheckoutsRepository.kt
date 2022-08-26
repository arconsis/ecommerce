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
	private val checkoutsDataStore: CheckoutsDataStore
) {
	fun createCheckout(order: Order, paymentStatus: CheckoutStatus, session: Mutiny.Session): Uni<Checkout> {
		val newCheckout = order.toCreateCheckout(order.paymentMethod.pspToken, paymentStatus)
		return checkoutsDataStore.createCheckout(newCheckout, session)
	}

	fun updateCheckout(checkoutId: UUID, paymentStatus: CheckoutStatus, session: Mutiny.Session): Uni<Checkout> {
		return checkoutsDataStore.updateCheckout(checkoutId, paymentStatus, session)
	}

	fun updateCheckout(
		checkoutId: UUID,
		paymentStatus: CheckoutStatus,
		paymentErrorMessage: String?,
		paymentErrorCode: String?,
		session: Mutiny.Session
	): Uni<Checkout> {
		return checkoutsDataStore.updateCheckout(checkoutId, paymentStatus, paymentErrorMessage, paymentErrorCode, session)
	}

	fun getCheckoutByOrderId(orderId: UUID, session: Mutiny.Session): Uni<Checkout?> {
		return checkoutsDataStore.getCheckoutByOrderId(orderId, session)
	}
}