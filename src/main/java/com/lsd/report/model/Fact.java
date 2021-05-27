package com.lsd.report.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Fact {
    String key;
    String value;
}
