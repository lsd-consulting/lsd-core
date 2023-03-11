package com.lsd.report.model

import com.lsd.diagram.ValidComponentName.of
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class ValidComponentNameTest {
    
    @ParameterizedTest
    @CsvSource(
        value = ["ShouldPreserveCamelCase:ShouldPreserveCamelCase", "Should remove spaces:ShouldRemoveSpaces", "removes  / illegal (characters):RemovesIllegalCharacters"],
        delimiter = ':'
    )
    fun generatesValidNameForMarkup(name: String, expected: String) {
        assertThat(of(name)).isEqualTo(expected)
    }
}