package com.lsd.diagram;

import org.apache.commons.text.WordUtils;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public class ValidComponentName {
    public static final String ILLEGAL_CHARS = "[()/]";

    public static String of(String name) {
        return stream(name
                .replaceAll(ILLEGAL_CHARS, " ")
                .split(" ")
        ).map(WordUtils::capitalize)
                .collect(joining())
                .replaceAll(" ", "");
    }
}
