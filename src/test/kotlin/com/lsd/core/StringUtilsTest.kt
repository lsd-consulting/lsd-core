package com.lsd.core

import com.lsd.core.properties.LsdProperties
import com.lsd.core.properties.LsdProperties.LABEL_MAX_WIDTH
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class StringUtilsTest {
    private val abbreviationLength = LsdProperties.getInt(LABEL_MAX_WIDTH)

    @Test
    fun stripsWhitespaceFromAbbreviatedLabel() {
        assertThat(" contains whitespace before and after ".abbreviate())
            .isEqualTo("contains whitespace before and after")
    }

    @Test
    fun abbreviatesLongStringToMaxLength() {
        assertThat("a".repeat(1000).abbreviate().length).isEqualTo(abbreviationLength)
    }

    @Test
    fun handleNullValuesSafely() {
        assertThat(null.sanitise()).isEqualTo("")
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = ',',
        value = [
            "input <\$something>,input",
            " abc,abc",
            "abc ,abc"
        ]
    )
    fun `sanitise() removes PlantUml markup`(input: String, expected: String) {
        assertThat(input.sanitise()).isEqualTo(expected)
    }
    
    @ParameterizedTest
    @CsvSource(
        delimiter = ',',
        value = [
            "<,&lt;",
            ">,&gt;",
            "you & me,you &amp; me",
            "I 'was' here,I &#x27;was&#x27; here",
            "my \"quote\",my &quot;quote&quot;",
            "<true>,&lt;true&gt;",
        ]
    )
    fun `escapes html`(input: String, expected: String) {
        assertThat(input.escapeHtml()).isEqualTo(expected)
    }
}