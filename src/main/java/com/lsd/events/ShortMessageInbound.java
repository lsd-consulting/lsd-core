package com.lsd.events;

import com.lsd.diagram.ValidComponentName;
import com.lsd.report.model.DataHolder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

import static com.lsd.Sanitiser.sanitise;
import static com.lsd.events.ArrowType.SOLID;

@Data
@SuperBuilder
public class ShortMessageInbound implements DataHolder {
    private final String id;
    private final String to;
    private final String label;

    @Default
    private final Object data = "";

    @Default
    private final String colour = "";

    @Default
    private final ArrowType arrowType = SOLID;

    @Override
    public String toMarkup() {
        return StringSubstitutor.replace("?${arrow} ${to}: <text fill=\"${colour}\">[[#${id} {${tooltip}} ${label}]]</text>", Map.of(
                "to", ValidComponentName.of(getTo()),
                "id", getId(),
                "tooltip", sanitise(getLabel()),
                "label", getAbbreviatedLabel(),
                "colour", getColour(),
                "arrow", getArrowType().toMarkup(getColour())
        ));
    }
}
