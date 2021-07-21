package com.lsd.diagram;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static net.sourceforge.plantuml.FileFormat.SVG;
import static org.apache.commons.lang3.StringUtils.countMatches;

public class SvgConverter {

    public String convert(String markup) {
        var result = new StringBuilder();
        var newmarkup = countMatches(markup, "@startuml");
        for (int i = 0; i < newmarkup; i++) {
            result.append(prettyPrint(createSvg(markup, i)));
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

    private String prettyPrint(String xml) {
        try {
            SAXBuilder sb = new SAXBuilder();
            Document doc = sb.build(new StringReader(xml));
            StringWriter stringWriter = new StringWriter();
            new XMLOutputter(Format.getPrettyFormat()).output(doc, stringWriter);
            return stringWriter.toString();
        } catch (JDOMException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}