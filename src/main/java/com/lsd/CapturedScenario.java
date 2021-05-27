package com.lsd;

import com.lsd.events.SequenceEvent;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class CapturedScenario {
    private final List<SequenceEvent> sequenceEvents = new ArrayList<>();

    private String title;
    private String description;

    public void add(SequenceEvent sequenceEvent) {
        sequenceEvents.add(sequenceEvent);
    }
}
