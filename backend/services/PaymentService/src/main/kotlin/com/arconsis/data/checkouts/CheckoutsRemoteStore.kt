package com.arconsis.data.checkouts

import com.arconsis.domain.orders.Order
import com.stripe.Stripe
import com.stripe.model.checkout.Session
import com.stripe.param.checkout.SessionCreateParams
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.math.BigDecimal
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CheckoutsRemoteStore(
	@ConfigProperty(name = "QUARKUS_STRIPE_API_KEY") private val apiKey: String,
) {
	// TODO: return CheckoutSession instead of stripe Session
	fun createCheckoutSession(orderId: UUID, order: Order): Session {
		Stripe.apiKey = apiKey
		val params: SessionCreateParams = SessionCreateParams.builder()
			.setMode(SessionCreateParams.Mode.PAYMENT)
			//    A success_url, a page on your website to redirect your customer after they complete the payment.
			//    A cancel_url, a page on your website to redirect your customer if they click on your logo in Checkout.
			.setSuccessUrl("https://example.com/success")
			.setCancelUrl("https://example.com/cancel")
			// A unique string to reference the Checkout Session. This can be a customer ID, a cart ID, or similar, and can be used to reconcile the Session with your internal systems.
			.setClientReferenceId(orderId.toString())
			.addAllLineItem(order.items.map { item ->
				val totalProductPrice = item.price.multiply(BigDecimal(1) + BigDecimal(order.prices.tax)).setScale(2)
				SessionCreateParams.LineItem.builder()
					.setQuantity(item.quantity.toLong())
					.setPriceData(
						SessionCreateParams.LineItem.PriceData.builder()
							.setCurrency(item.currency)
							.setUnitAmount(totalProductPrice.movePointRight(2).intValueExact().toLong())
							.setProductData(
								SessionCreateParams.LineItem.PriceData.ProductData.builder()
									.setName(item.productName)
									.addImage(item.thumbnail)
									.build(),
							)
							.build()
					)
					.build()
				}
			)
				// TODO: we should gather tax based on shipping address of the user / customer -> https://stripe.com/docs/payments/checkout/taxes?tax-calculation=stripe-tax#existing-customers
//			.setAutomaticTax(
//				SessionCreateParams.AutomaticTax.builder()
//					.setEnabled(true)
//					.build())
			.putMetadata("order_id", orderId.toString())
			.setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder().putMetadata("order_id", orderId.toString()).build())
			.build()
		return Session.create(params)
	}
}

