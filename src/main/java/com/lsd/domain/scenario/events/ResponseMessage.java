package com.lsd.domain.scenario.events;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

@Value
@Builder
public class ResponseMessage implements Interaction {
    String id;
    String label;
    String from;
    String to;
    Object data;

    public String toMarkup() {
        return StringSubstitutor.replace("${from} -->> ${to}:[[#${id} ${label}]]", Map.of(
                "from", getFrom(),
                "to", getTo(),
                "id", getId(),
                "label", abbreviatedLabel()));
    }
}
