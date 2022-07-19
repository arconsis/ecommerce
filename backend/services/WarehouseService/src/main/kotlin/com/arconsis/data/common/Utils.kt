package com.arconsis.data.common

import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.groups.UniAndGroup2

fun <T> T.toUni(): Uni<T> = Uni.createFrom().item(this)

fun <T, R> UniAndGroup2<T, R>.asPair(): Uni<Pair<T, R>> =
	combinedWith { first, second -> Pair(first, second) }