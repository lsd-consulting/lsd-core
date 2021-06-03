package com.lsd.events;

import com.lsd.report.model.DataHolder;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

import static com.lsd.events.ArrowType.SOLID;

@Value
@Builder
public class Message implements DataHolder {
    String id;
    String from;
    String to;
    String label;

    @Default
    Object data = "";

    @Default
    String colour = "";

    @Default
    ArrowType arrowType = SOLID;

    public String toMarkup() {
        return StringSubstitutor.replace("${from} ${arrow} ${to}: <text fill=\"${colour}\">[[#${id} ${label}]]</text>", Map.of(
                "from", getFrom(),
                "to", getTo(),
                "id", getId(),
                "label", abbreviatedLabel(),
                "colour", getColour(),
                "arrow", arrowType.toMarkup(getColour())
        ));
    }

}
