package com.arconsis.presentation.http.payments

import com.arconsis.domain.checkoutevents.CheckoutEvent
import com.arconsis.domain.checkoutevents.CheckoutEventsService
import com.arconsis.presentation.http.payments.dto.CheckoutEventDto
import com.arconsis.presentation.http.payments.dto.CreateCheckoutEventDto
import com.fasterxml.jackson.databind.ObjectMapper
import com.stripe.model.*
import com.stripe.net.ApiResource
import com.stripe.net.Webhook
import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestHeader
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*
import javax.ws.rs.core.*

@ApplicationScoped
@Path("/payments")
class PaymentsResource(
	@ConfigProperty(name = "QUARKUS_STRIPE_WEBHOOK_SECRET") private val endpointSecret: String,
	private val checkoutEventsService: CheckoutEventsService,
	private val objectMapper: ObjectMapper,
	private val logger: Logger
) {
	@POST
	@Path("/webhooks")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	fun createOrder(
		@RestHeader("Stripe-Signature") stripeSignature: String,
		@Context uriInfo: UriInfo,
		payload: String
	): Uni<CheckoutEvent?> {
		logger.info("Got webhook event $payload")
		try {
			Webhook.constructEvent(payload, stripeSignature, endpointSecret)
			val event = ApiResource.GSON.fromJson(payload, Event::class.java)
			val dataObjectDeserializer: EventDataObjectDeserializer = event.dataObjectDeserializer
			if (dataObjectDeserializer.getObject().isPresent) {
				val checkoutEventDto = objectMapper.readValue(
					payload,
					CheckoutEventDto::class.java
				)
				val enrichedCheckoutEventDto = CreateCheckoutEventDto(
					type = checkoutEventDto.type,
					orderId = checkoutEventDto.data.entity.metadata.orderId,
					pspData = payload,
					pspReferenceId = checkoutEventDto.data.entity.pspReferenceId,
					paymentErrorCode = checkoutEventDto.data.entity.paymentErrorCode,
					paymentErrorMessage = checkoutEventDto.data.entity.paymentErrorMessage,
				)
				return checkoutEventsService.createCheckoutEvent(enrichedCheckoutEventDto)
			} else {
				// Deserialization failed, probably due to an API version mismatch.
				// Refer to the Javadoc documentation on `EventDataObjectDeserializer`
				throw BadRequestException("Event is not correct")
			}
		} catch (e: Exception) {
			// Invalid payload
			throw BadRequestException("Event is not correct")
		}
	}
}
