package com.lsd.events;

import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import static com.lsd.events.ArrowType.DOTTED_THIN;

/**
 * Represents a synchronous response message (e.g. A HTTP response to a request)
 * <p>
 * This type of message will get filtered out when generating component diagrams since the response is an
 * indirect message to the recipient.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public class SynchronousResponse extends Message {

    @Default
    private final ArrowType arrowType = DOTTED_THIN;
}
