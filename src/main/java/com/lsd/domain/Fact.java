package com.lsd.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Fact {
    String key;
    String value;
}
