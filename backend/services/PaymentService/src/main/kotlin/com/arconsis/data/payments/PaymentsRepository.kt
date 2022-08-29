package com.arconsis.data.payments

import com.arconsis.domain.orders.Order
import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRepository(
	@RestClient private val paymentsRemoteStore: PaymentsRemoteStore,
	private val logger: Logger
) {
	fun charge(order: Order): Uni<Void> {
		return paymentsRemoteStore.charge(
			amount = order.prices.totalPrice.movePointRight(2).intValueExact().toLong().toString(),
			currency = order.prices.currency.name,
			source = order.paymentMethod.pspToken,
			description = "Payment for order with id: ${order.orderId} for user with id: ${order.userId}",
			orderId = order.orderId.toString()
		).map { null }
			.onFailure {
				logger.error("Payment for order with id: ${order.orderId} failed because of:", it)
				false
			}.recoverWithNull().map { null }
	}
}