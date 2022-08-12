package com.arconsis.common

import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.groups.UniAndGroup2
import java.time.Duration

fun <T> Uni<T>.retryWithBackoff(duration: Duration = Duration.ofSeconds(3), atMost: Long = 3): Uni<T> = onFailure()
    .retry()
    .withBackOff(duration)
    .atMost(atMost)

fun <T> T.toUni(): Uni<T> = Uni.createFrom().item(this)

fun <T, R> UniAndGroup2<T, R>.asPair(): Uni<Pair<T, R>> =
    combinedWith { first, second -> Pair(first, second) }