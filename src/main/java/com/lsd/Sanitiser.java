package com.lsd;

import java.util.Optional;

public class Sanitiser {

    /**
     * Remove markup that doesn't render nicely outside of context (e.g. plantUml placeholders such as
     * <pre>
     * {@code
     * <$..>
     * }
     * </pre>
     * being displayed on the html.
     *
     * @param input The String value to sanitise
     * @return The sanitised version of the input text, if the input value is null an empty string is returned.
     */
    public static String sanitise(String input) {
        return Optional.ofNullable(input)
                .map(value -> value.replaceAll("<\\$.*?>", "")).
                orElse("");
    }
}
