package com.lsd

import java.util.UUID.randomUUID
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Supplier

class IdGenerator(isDeterministic: Boolean = false) {
    private val counter = AtomicInteger()
    
    private val idSupplier: Supplier<String> = if (isDeterministic)
        Supplier { counter.incrementAndGet().toString() }
    else
        Supplier { randomUUID().toString().replace("-".toRegex(), "") }


    operator fun next(): String = idSupplier.get()

    /**
     * Restarts the counter - only works when in deterministic mode
     */
    fun reset() = counter.set(0)
}