package com.lsd.domain.scenario;

import com.lsd.domain.scenario.diagram.SequenceDiagram;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;

@Value
@Builder
public class Scenario {
    String title;
    String description;
    SequenceDiagram sequenceDiagram;
    Collection<Fact> facts;
}
