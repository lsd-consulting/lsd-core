package com.lsd.core.domain

import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class ComponentNameTest {

    @ParameterizedTest
    @CsvSource(
        value = ["ShouldPreserveCamelCase:ShouldPreserveCamelCase", "Should remove spaces:ShouldRemoveSpaces", "removes  / illegal (characters):RemovesIllegalCharacters"],
        delimiter = ':'
    )
    fun generatesValidNameForMarkup(raw: String, expected: String) {
        Assertions.assertThat(ComponentName(raw).normalisedName).isEqualTo(expected)
    }
}