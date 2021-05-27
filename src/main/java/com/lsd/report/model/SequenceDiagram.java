package com.lsd.report.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class SequenceDiagram {
    String uml;
    String svg;
}
