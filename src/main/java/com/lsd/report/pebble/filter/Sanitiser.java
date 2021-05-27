package com.lsd.report.pebble.filter;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.util.List;
import java.util.Map;

/**
 * Remove markup that doesn't render nicely outside of context (e.g. plantUml placeholders such as
 * <pre>
 * {@code
 * <$..>
 * }
 * </pre>
 * being displayed on the html.
 */
public class Sanitiser implements Filter {

    @Override
    public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) throws PebbleException {
        if (input instanceof String) {
            return ((String) input).replaceAll("<\\$.*?>", "");
        }
        return input;
    }

    @Override
    public List<String> getArgumentNames() {
        return null;
    }
}
