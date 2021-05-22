package com.lsd.domain;

import com.lsd.PlantUmlRenderer;
import com.lsd.SvgConverter;
import com.lsd.domain.interactions.Interaction;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;

@Value
@Builder
public class Scenario {
    String title;
    String description;
    Collection<Interaction> interactions;
    Collection<Fact> facts;

    PlantUmlRenderer plantUmlRenderer = new PlantUmlRenderer();
    SvgConverter svgConverter = new SvgConverter();

    public String sequenceDiagram() {
        String plantUml = plantUmlRenderer.render(interactions);
        return svgConverter.convert(plantUml);
    }
}
