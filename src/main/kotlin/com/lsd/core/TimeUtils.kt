package com.lsd.core

import java.time.Duration
import java.time.Instant


fun <R> timedResult(block: () -> R): Pair<Duration, R> {
    val start = Instant.now()
    val result = block.invoke()
    val stop = Instant.now()

    return Duration.between(start, stop) to result
}
