package com.lsd;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static java.util.UUID.randomUUID;

public class IdGenerator {
    private final AtomicInteger counter = new AtomicInteger();

    private final Supplier<String> idSupplier;

    public IdGenerator(boolean isDeterministic) {
        if (isDeterministic) {
            idSupplier = () -> String.valueOf(counter.incrementAndGet());
        } else {
            idSupplier = () -> randomUUID().toString()
                    .replaceAll("-", "");
        }
    }

    public String next() {
        return idSupplier.get();
    }

    /**
     * Restarts the counter - only works when in deterministic mode
     */
    public void reset() {
        counter.set(0);
    }
}
