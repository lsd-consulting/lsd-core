package com.lsd.report.model

import com.lsd.events.Message
import com.lsd.properties.LsdProperties
import com.lsd.properties.LsdProperties.LABEL_MAX_WIDTH
import org.apache.commons.lang3.RandomStringUtils.random
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DataHolderTest {
    private val abbreviationLength = LsdProperties.getInt(LABEL_MAX_WIDTH)
    private val dataHolder: DataHolder =
        Message(
            label = " contains whitespace before and after ",
            id = random(5),
            from = random(5),
            to = random(5)
        )

    @Test
    fun stripsWhitespaceFromAbbreviatedLabel() {
        assertThat(dataHolder.abbreviatedLabel).isEqualTo("contains whitespace before and after")
    }

    @Test
    fun abbreviatesLabelToMaxLength() {
        val messageWithLongLabel = Message(
            label = "a".repeat(1000),
            id = random(5),
            from = random(5),
            to = random(5),
        )
        assertThat(messageWithLongLabel.abbreviatedLabel.length).isEqualTo(abbreviationLength)
    }
}