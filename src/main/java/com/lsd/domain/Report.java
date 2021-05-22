package com.lsd.domain;

import lombok.Builder;
import lombok.Value;

import java.util.Collection;

@Value
@Builder
public class Report {
    String title;
    Collection<Scenario> scenarios;
}
