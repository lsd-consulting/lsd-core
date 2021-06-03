package com.lsd;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdGeneratorTest {

    @Test
    void generateDeterministicIds() {
        var idGenerator1 = new IdGenerator(true);
        var idGenerator2 = new IdGenerator(true);

        assertThat(idGenerator1.next()).isEqualTo(idGenerator2.next());
    }

    @Test
    void generatesDifferentIds() {
        var idGenerator = new IdGenerator(true);

        assertThat(idGenerator.next()).isNotEqualTo(idGenerator.next());
    }

    @Test
    void generateNonDeterministicIds() {
        var idGenerator1 = new IdGenerator(false);
        var idGenerator2 = new IdGenerator(false);
        var idGenerator3 = new IdGenerator(false);

        assertThat(idGenerator1.next())
                .isNotEqualTo(idGenerator2.next())
                .isNotEqualTo(idGenerator3.next());
    }
}