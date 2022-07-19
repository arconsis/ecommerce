package com.arconsis.common

import io.smallrye.mutiny.Uni
import java.time.Duration

fun <T> T.toUni(): Uni<T> = Uni.createFrom().item(this)
