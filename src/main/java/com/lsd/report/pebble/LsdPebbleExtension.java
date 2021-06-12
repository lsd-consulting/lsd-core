package com.lsd.report.pebble;

import com.lsd.report.pebble.filter.SanitiserFilter;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;

import java.util.Map;

public class LsdPebbleExtension extends AbstractExtension {
    @Override
    public Map<String, Filter> getFilters() {
        return Map.of(
                "sanitise", new SanitiserFilter()
        );
    }
}
