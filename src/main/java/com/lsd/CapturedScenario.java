package com.lsd;

import com.lsd.events.SequenceEvent;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@NoArgsConstructor
@Data
public class CapturedScenario {
    private final List<SequenceEvent> sequenceEvents = new ArrayList<>();
    private final MultiValuedMap<String, String> facts = new ArrayListValuedHashMap<>();

    private String title;
    private String description;
    private OutcomeStatus outcomeStatus;

    public void add(SequenceEvent sequenceEvent) {
        if (nonNull(sequenceEvent)) {
            sequenceEvents.add(sequenceEvent);
        }
    }

    public void addFact(String key, String value) {
        facts.put(key, value);
    }

}
