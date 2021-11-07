package com.lsd.diagram;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static lsd.format.xml.XmlPrettyPrinter.indentXml;
import static net.sourceforge.plantuml.FileFormat.SVG;
import static org.apache.commons.lang3.StringUtils.countMatches;

public class SvgConverter {

    public String convert(String markup) {
        var result = new StringBuilder();
        var newmarkup = countMatches(markup, "@startuml");
        for (int i = 0; i < newmarkup; i++) {
            var svg = createSvg(markup, i);
            result.append(indentXml(svg).orElse(svg));
        }
        return result.toString();
    }

    private String createSvg(String plantUmlMarkup, int pageNumber) {
        try (var os = new ByteArrayOutputStream()) {
            var reader = new SourceStringReader(plantUmlMarkup);
            reader.outputImage(os, pageNumber, new FileFormatOption(SVG, false));
            return os.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}