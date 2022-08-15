package com.arconsis.common

import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.groups.UniAndGroup2
import java.time.Duration
import javax.ws.rs.core.Response

fun <T> Uni<T>.retryWithBackoff(duration: Duration = Duration.ofSeconds(3), atMost: Long = 3): Uni<T> = onFailure()
	.retry()
	.withBackOff(duration)
	.atMost(atMost)

fun <T> T.toUni(): Uni<T> = Uni.createFrom().item(this)

fun <T, R> UniAndGroup2<T, R>.asPair(): Uni<Pair<T, R>> =
	combinedWith { first, second -> Pair(first, second) }

inline fun <reified T> Response.body(statusCodeRange: IntRange = 200..299, onError: (Response) -> T? = { null }): T? =
	when (this.status) {
		in statusCodeRange -> {
			bufferEntity()
			readEntity(T::class.java)
		}
		else -> onError(this)
	}
