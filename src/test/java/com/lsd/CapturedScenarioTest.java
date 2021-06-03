package com.lsd;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CapturedScenarioTest {

    private CapturedScenario capturedScenario = new CapturedScenario();

    @Test
    void excludeNullEvents() {
        capturedScenario.add(null);

        assertThat(capturedScenario.getSequenceEvents()).isEmpty();
    }
}