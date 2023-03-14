package com.lsd.core

import java.util.UUID.randomUUID
import java.util.concurrent.atomic.AtomicInteger

private val randomIdSupplier: () -> String = { randomUUID().toString().replace("-".toRegex(), "") }

class IdGenerator(isDeterministic: Boolean = false) {
    private val counter = AtomicInteger()
    private val deterministicIdSupplier: () -> String = { counter.incrementAndGet().toString() }

    private val idSupplier: () -> String = if (isDeterministic) deterministicIdSupplier else randomIdSupplier

    fun next(): String = idSupplier.invoke()

    /**
     * Restarts the counter - only works when in deterministic mode
     */
    fun reset() = counter.set(0)
}