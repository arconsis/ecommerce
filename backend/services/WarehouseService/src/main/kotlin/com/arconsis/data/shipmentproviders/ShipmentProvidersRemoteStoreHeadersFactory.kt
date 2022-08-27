package com.arconsis.data.shipmentproviders

import com.arconsis.common.addHeader
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory
import javax.ws.rs.core.MultivaluedHashMap
import javax.ws.rs.core.MultivaluedMap

class ShipmentProvidersRemoteStoreHeadersFactory(
	@ConfigProperty(name = "QUARKUS_SHIPPO_API_TOKEN") private val apiKey: String,
): ClientHeadersFactory {
	override fun update(incomingHeaders: MultivaluedMap<String, String>?, clientOutgoingHeaders: MultivaluedMap<String, String>?): MultivaluedMap<String, String> {
		val clientHeaders = MultivaluedHashMap<String, String>()
		"ShippoToken $apiKey".addHeader("Authorization", clientHeaders)

		return clientHeaders
	}
}