package com.lsd.pebble;

import com.lsd.pebble.filter.Sanitise;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;

import java.util.Map;

public class LsdPebbleExtension extends AbstractExtension {
    @Override
    public Map<String, Filter> getFilters() {
        return Map.of(
                "sanitise", new Sanitise()
        );
    }
}
