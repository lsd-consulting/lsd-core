package com.lsd.report.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Diagram {
    String id;
    String uml;
    String svg;
}
