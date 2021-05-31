package com.lsd.report.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class SequenceDiagram {
    String id;
    String uml;
    String svg;
}
