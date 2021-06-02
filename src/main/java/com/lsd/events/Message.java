package com.lsd.events;

import com.lsd.report.model.DataHolder;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

import static com.lsd.events.Message.ArrowType.SOLID;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Value
@Builder
public class Message implements DataHolder {
    String from;
    String to;
    String label;

    @Default
    String id = randomUUID().toString()
            .replaceAll("-", "");

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

    public enum ArrowType {
        SOLID("-%s>"),
        BI_DIRECTIONAL("<-%s>"),
        BI_DIRECTIONAL_DOTTED("<-%s->"),
        LOST("-%s>x"),
        DOTTED("--%s>"),
        DOTTED_THIN("--%s>>");

        private String markup;

        ArrowType(String markup) {
            this.markup = markup;
        }

        public String toMarkup(String colour) {
            var colourMarkup = isEmpty(colour) ? "" : "[#" + colour + "]";
            return String.format(markup, colourMarkup);
        }
    }
}
