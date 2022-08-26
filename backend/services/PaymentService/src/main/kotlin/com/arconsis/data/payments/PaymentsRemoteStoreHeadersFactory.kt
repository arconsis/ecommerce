package com.arconsis.data.payments

import com.arconsis.common.addHeader
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory
import java.util.*
import javax.ws.rs.core.MultivaluedHashMap
import javax.ws.rs.core.MultivaluedMap

class PaymentsRemoteStoreHeadersFactory(
	@ConfigProperty(name = "QUARKUS_STRIPE_API_KEY") private val apiKey: String,
): ClientHeadersFactory {
	override fun update(incomingHeaders: MultivaluedMap<String, String>?, clientOutgoingHeaders: MultivaluedMap<String, String>?): MultivaluedMap<String, String> {
		val clientHeaders = MultivaluedHashMap<String, String>()

		val basicToken = String(Base64.getEncoder().encode("${apiKey}:".toByteArray()))
		"Basic $basicToken".addHeader("Authorization", clientHeaders)

		return clientHeaders
	}
}
