package com.lsd.report.model;

import com.lsd.diagram.ValidComponentName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class ValidComponentNameTest {

    @ParameterizedTest
    @CsvSource(value = {
            "ShouldPreserveCamelCase:ShouldPreserveCamelCase",
            "Should remove spaces:ShouldRemoveSpaces",
            "removes  / illegal (characters):RemovesIllegalCharacters"
    }, delimiter = ':')
    void generatesValidNameForMarkup(String name, String expected) {
        assertThat(ValidComponentName.of(name)).isEqualTo(expected);
    }
}
