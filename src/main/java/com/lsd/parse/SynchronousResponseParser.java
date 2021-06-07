package com.lsd.parse;

import com.lsd.IdGenerator;
import com.lsd.events.SequenceEvent;
import com.lsd.events.SynchronousResponse;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lsd.events.ArrowType.DOTTED_THIN;
import static java.util.Objects.isNull;
import static java.util.regex.Pattern.compile;

public class SynchronousResponseParser implements Parser {

    private static final Pattern PATTERN = compile("^sync ?(.*?) from (.*?) to (.*?)(?: \\[#(.*)\\])?$");

    private final IdGenerator idGenerator;

    public SynchronousResponseParser(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Optional<SequenceEvent> parse(String message, String body) {
        Matcher matcher = PATTERN.matcher(message);
        if (matcher.matches()) {
            var colour = matcher.group(4);
            return Optional.of(SynchronousResponse.builder()
                    .id(idGenerator.next())
                    .data(isNull(body) ? "" : body)
                    .label(matcher.group(1))
                    .from(matcher.group(2))
                    .to(matcher.group(3))
                    .colour(isNull(colour) ? "" : colour)
                    .arrowType(DOTTED_THIN)
                    .build());
        }
        return Optional.empty();
    }
}
