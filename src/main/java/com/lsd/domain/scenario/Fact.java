package com.lsd.domain.scenario;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Fact {
    String key;
    String value;
}
