package com.lsd.events;

import com.lsd.report.model.DataHolder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

import static com.lsd.events.ArrowType.SOLID;

@Data
@SuperBuilder
public class Message implements DataHolder {
    private final String id;
    private final String from;
    private final String to;
    private final String label;

    @Default
    private final Object data = "";

    @Default
    private final String colour = "";

    @Default
    private final ArrowType arrowType = SOLID;

    public String toMarkup() {
        return StringSubstitutor.replace("${from} ${arrow} ${to}: <text fill=\"${colour}\">[[#${id} {${tooltip}} ${label}]]</text>", Map.of(
                "from", getFrom(),
                "to", getTo(),
                "id", getId(),
                "tooltip", getLabel().replaceAll("<\\$.*?>", ""),
                "label", abbreviatedLabel(),
                "colour", getColour(),
                "arrow", getArrowType().toMarkup(getColour())
        ));
    }

}
