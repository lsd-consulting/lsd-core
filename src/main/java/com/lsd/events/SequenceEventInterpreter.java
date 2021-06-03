package com.lsd.events;

import com.lsd.IdGenerator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lsd.events.ArrowType.DOTTED_THIN;
import static com.lsd.events.ArrowType.SOLID;
import static java.util.Objects.isNull;
import static java.util.regex.Pattern.compile;

public class SequenceEventInterpreter {

    private static final Pattern MESSAGE_PATTERN = compile("(.*) from (.*) to (.*?)(?: \\[#(.*)\\])?$");

    private final IdGenerator idGenerator;

    public SequenceEventInterpreter(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public SequenceEvent interpret(String message) {
        return interpret(message, null);
    }

    public SequenceEvent interpret(String message, String body) {
        Matcher matcher = MESSAGE_PATTERN.matcher(message);
        if (matcher.matches()) {
            var colour = matcher.group(4);
            var label = matcher.group(1);
            var arrowType = label.contains("response") ? DOTTED_THIN : SOLID;
            
            return Message.builder()
                    .id(idGenerator.next())
                    .data(isNull(body) ? "" : body)
                    .label(label)
                    .from(matcher.group(2))
                    .to(matcher.group(3))
                    .colour(isNull(colour) ? "" : colour)
                    .arrowType(arrowType)
                    .build();
        }
        return null;
    }
}
