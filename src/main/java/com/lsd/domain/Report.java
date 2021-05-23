package com.lsd.domain;

import com.lsd.domain.scenario.Scenario;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;

@Value
@Builder
public class Report {
    String title;
    Collection<Scenario> scenarios;
}
