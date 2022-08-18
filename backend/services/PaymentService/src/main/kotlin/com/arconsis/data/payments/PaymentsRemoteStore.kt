package com.arconsis.data.payments

import com.arconsis.domain.orders.Order
import com.stripe.Stripe
import com.stripe.model.Charge
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.infrastructure.Infrastructure
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class PaymentsRemoteStore(
	@ConfigProperty(name = "QUARKUS_STRIPE_API_KEY") private val apiKey: String,
	private val logger: Logger,
) {
	fun charge(order: Order): Uni<Charge?> = Uni.createFrom().item {
			performPayment(order)
		}.runSubscriptionOn(Infrastructure.getDefaultWorkerPool())


	private fun performPayment(order: Order): Charge? {
		Stripe.apiKey = apiKey
		val params: MutableMap<String, Any> = HashMap()
		val metadataParams: MutableMap<String, Any> = HashMap()
		params["amount"] = order.prices.totalPrice.movePointRight(2).intValueExact().toLong()
		params["currency"] = order.prices.currency
		params["source"] = order.paymentMethod.pspToken
		params["description"] = "Payment for order with id: ${order.orderId} for user with id: ${order.userId}"
		metadataParams["order_id"] = order.orderId
		params["metadata"] = metadataParams
		return Charge.create(params)
	}
}
