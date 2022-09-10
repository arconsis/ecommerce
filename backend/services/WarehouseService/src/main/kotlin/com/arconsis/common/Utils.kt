package com.arconsis.common

import io.smallrye.mutiny.Uni
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

suspend fun <A, B> Iterable<A>.pmap(chunkSize: Int = 24, f: suspend (A) -> B): List<B> {
	val batches = this.chunked(chunkSize)
	return coroutineScope {
		batches.flatMap{ deferredList ->
			deferredList.map {
				async { f(it) }
			}.awaitAll()
		}
	}
}

fun String.toSlug() = toLowerCase()
	.replace("\n", " ")
	.replace("[^a-z\\d\\s]".toRegex(), " ")
	.split(" ")
	.joinToString("-")
	.replace("-+".toRegex(), "-")