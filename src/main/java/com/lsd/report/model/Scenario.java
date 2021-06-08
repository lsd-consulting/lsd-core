package com.lsd.report.model;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.collections4.MultiValuedMap;

import java.util.List;

/**
 * Beware that these names are use in the html template so be careful when renaming.
 */
@Value
@Builder
public class Scenario {
    String title;
    String id;
    String description;
    String status;
    Diagram sequenceDiagram;
    Diagram componentDiagram;
    List<DataHolder> dataHolders;
    MultiValuedMap<String, String> facts;
}
