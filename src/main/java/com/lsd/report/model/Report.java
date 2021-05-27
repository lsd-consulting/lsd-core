package com.lsd.report.model;

import lombok.Builder;
import lombok.Value;

import java.util.Collection;

@Value
@Builder
public class Report {
    String title;
    Collection<Scenario> scenarios;
}
