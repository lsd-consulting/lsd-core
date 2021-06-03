package com.lsd.events;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

public enum ArrowType {
    SOLID("-%s>"),
    BI_DIRECTIONAL("<-%s>"),
    BI_DIRECTIONAL_DOTTED("<-%s->"),
    LOST("-%s>x"),
    DOTTED("--%s>"),
    DOTTED_THIN("--%s>>");

    private String markup;

    ArrowType(String markup) {
        this.markup = markup;
    }

    public String toMarkup(String colour) {
        var colourMarkup = isEmpty(colour) ? "" : "[#" + colour + "]";
        return String.format(markup, colourMarkup);
    }
}
