package com.lsd.report.pebble.filter;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.util.List;
import java.util.Map;

import static com.lsd.Sanitiser.sanitise;

/**
 * Makes the sanitiser available to the pebble templates as a filter. (See {@link com.lsd.Sanitiser}).
 */
public class SanitiserFilter implements Filter {

    @Override
    public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) throws PebbleException {
        if (input instanceof String) {
            return sanitise((String) input);
        }
        return input;
    }

    @Override
    public List<String> getArgumentNames() {
        return null;
    }
}
