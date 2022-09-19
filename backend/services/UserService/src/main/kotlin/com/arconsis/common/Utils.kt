package com.arconsis.common

import io.smallrye.mutiny.Uni
import javax.ws.rs.core.Response

fun <T> T.toUni(): Uni<T> = Uni.createFrom().item(this)

fun Response.bodyAsString(): String? = try {
	bufferEntity()
	readEntity(String::class.java)
} catch (e: Exception) {
	null
}

inline fun <reified T> Response.body(statusCodeRange: IntRange = 200..299, onError: (Response) -> T? = { null }): T? = when (this.status) {
	in statusCodeRange -> {
		bufferEntity()
		readEntity(T::class.java)
	}
	else               -> onError(this)
}