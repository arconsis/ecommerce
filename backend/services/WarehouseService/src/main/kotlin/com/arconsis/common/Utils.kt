package com.arconsis.common

import io.smallrye.mutiny.Uni
import java.time.Duration
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response

fun <T> T.toUni(): Uni<T> = Uni.createFrom().item(this)

fun String?.addHeader(name: String, clientHeaders: MultivaluedMap<String, String>) {
	if (this != null) {
		clientHeaders.putSingle(name, this)
	}
}

fun String?.addHeader(name: String, clientHeaders: MutableMap<String, String>) {
	if (this != null) {
		clientHeaders[name] = this
	}
}

inline fun <reified T> Response.body(statusCodeRange: IntRange = 200..299, onError: (Response) -> T? = { null }): T? =
	when (this.status) {
		in statusCodeRange -> {
			bufferEntity()
			readEntity(T::class.java)
		}
		else -> onError(this)
	}

fun Response.bodyAsString(): String? = try {
	bufferEntity()
	readEntity(String::class.java)
} catch (e: Exception) {
	null
}