package com.lsd.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class IdGeneratorTest {
    @Test
    fun generateDeterministicIds() {
        val idGenerator1 = IdGenerator(true)
        val idGenerator2 = IdGenerator(true)

        assertThat(idGenerator1.next()).isEqualTo(idGenerator2.next())
    }

    @Test
    fun generatesDifferentIds() {
        val idGenerator = IdGenerator(true)

        assertThat(idGenerator.next()).isNotEqualTo(idGenerator.next())
    }

    @Test
    fun generateNonDeterministicIds() {
        val idGenerator1 = IdGenerator(false)
        val idGenerator2 = IdGenerator(false)
        val idGenerator3 = IdGenerator(false)

        assertThat(idGenerator1.next())
            .isNotEqualTo(idGenerator2.next())
            .isNotEqualTo(idGenerator3.next())
    }
}