package com.lsd.parse;

import com.lsd.IdGenerator;
import com.lsd.events.Message;
import com.lsd.events.SequenceEvent;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lsd.events.ArrowType.SOLID;
import static java.util.Objects.isNull;
import static java.util.regex.Pattern.compile;

@AllArgsConstructor
public class MessageParser implements Parser {

    private static final Pattern PATTERN = compile("^(?!sync)(.*?) from (.*?) to (.*?)(?: \\[#(.*)\\])?$");

    private final IdGenerator idGenerator;

    public Optional<SequenceEvent> parse(String message, String body) {
        Matcher matcher = PATTERN.matcher(message);
        if (matcher.matches()) {
            var colour = matcher.group(4);
            return Optional.of(Message.builder()
                    .id(idGenerator.next())
                    .data(isNull(body) ? "" : body)
                    .label(matcher.group(1))
                    .from(matcher.group(2))
                    .to(matcher.group(3))
                    .colour(isNull(colour) ? "" : colour)
                    .arrowType(SOLID)
                    .build());
        }
        return Optional.empty();
    }
}
