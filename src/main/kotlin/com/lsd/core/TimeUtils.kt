package com.lsd.core

import java.time.Duration
import java.time.Instant
import kotlin.time.toKotlinDuration


fun <R> timedResult(block: () -> R): Pair<kotlin.time.Duration, R> {
    val start = Instant.now()
    val result = block.invoke()
    val stop = Instant.now()

    return Duration.between(start, stop).toKotlinDuration() to result
}
