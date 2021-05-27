package com.lsd.report.model;

import lombok.Builder;
import lombok.Value;

import java.util.Collection;
import java.util.List;

/**
 * Beware that these names are use in the html template so be careful when renaming.
 */
@Value
@Builder
public class Scenario {
    String title;
    String description;
    SequenceDiagram sequenceDiagram;
    List<DataHolder> dataHolders;
    Collection<Fact> facts;
}
