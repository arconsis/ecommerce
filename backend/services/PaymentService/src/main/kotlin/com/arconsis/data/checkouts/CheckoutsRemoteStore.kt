package com.arconsis.data.checkouts

import com.arconsis.domain.orders.OrderItem
import com.stripe.Stripe
import com.stripe.model.checkout.Session
import com.stripe.param.checkout.SessionCreateParams
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CheckoutsRemoteStore(
	@ConfigProperty(name = "QUARKUS_STRIPE_API_KEY") private val apiKey: String,
) {
	// TODO: return CheckoutSession instead of stripe Session
	fun createCheckoutSession(orderId: UUID, orderItems: List<OrderItem>): Session {
		Stripe.apiKey = apiKey
		val params: SessionCreateParams = SessionCreateParams.builder()
			.setMode(SessionCreateParams.Mode.PAYMENT)
			//    A success_url, a page on your website to redirect your customer after they complete the payment.
			//    A cancel_url, a page on your website to redirect your customer if they click on your logo in Checkout.
			.setSuccessUrl("https://example.com/success")
			.setCancelUrl("https://example.com/cancel")
			// A unique string to reference the Checkout Session. This can be a customer ID, a cart ID, or similar, and can be used to reconcile the Session with your internal systems.
			.setClientReferenceId(orderId.toString())
			.addAllLineItem(orderItems.map { item ->
				SessionCreateParams.LineItem.builder()
					.setQuantity(item.quantity.toLong())
					.setPriceData(
						SessionCreateParams.LineItem.PriceData.builder()
							.setCurrency(item.currency)
							.setUnitAmount(item.price.movePointRight(2).intValueExact().toLong())
							.setProductData(
								SessionCreateParams.LineItem.PriceData.ProductData.builder()
									.setName(item.productName)
									.build(),
							)
							.build()
					)
					.build()
				}
			)
			.build()
		return Session.create(params)
	}

}

