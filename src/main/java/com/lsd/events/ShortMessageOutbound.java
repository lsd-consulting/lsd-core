package com.lsd.events;

import com.lsd.report.model.DataHolder;
import com.lsd.diagram.ValidComponentName;
import lombok.Builder.Default;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

import static com.lsd.Sanitiser.sanitise;
import static com.lsd.events.ArrowType.SOLID;

@Data
@SuperBuilder
public class ShortMessageOutbound implements DataHolder {
    private final String id;
    private final String from;
    private final String label;

    @Default
    private final Object data = "";

    @Default
    private final String colour = "";

    @Default
    private final ArrowType arrowType = SOLID;

    @Override
    public String toMarkup() {
        return StringSubstitutor.replace("${from} ${arrow}?: <text fill=\"${colour}\">[[#${id} {${tooltip}} ${label}]]</text>", Map.of(
                "from", ValidComponentName.of(getFrom()),
                "id", getId(),
                "tooltip", sanitise(getLabel()),
                "label", abbreviatedLabel(),
                "colour", getColour(),
                "arrow", getArrowType().toMarkup(getColour())
        ));
    }
}
