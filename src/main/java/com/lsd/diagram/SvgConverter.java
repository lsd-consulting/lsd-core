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

public class SvgConverter {

    public String convert(String markup) {
        return prettyPrint(createSvg(markup));
    }

    private String createSvg(String plantUmlMarkup) {
        SourceStringReader reader = new SourceStringReader(plantUmlMarkup);
        try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            reader.outputImage(os, new FileFormatOption(SVG, false));
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