package com.lsd.domain.scenario.diagram;

import com.lsd.SvgConverter;
import com.lsd.domain.scenario.events.Interaction;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class SequenceDiagram implements Diagram {
    SvgConverter svgConverter = new SvgConverter();

    String uml;
    List<Interaction> interactions;

    @Override
    public String toSvg() {
        return svgConverter.convert(uml);
    }
}
