# LSD Core

[![Build](https://github.com/nickmcdowall/lsd-core/actions/workflows/macos-build.yml/badge.svg)](https://github.com/nickmcdowall/lsd-core/actions/workflows/macos-build.yml)

This library can be used to capture data from tests or other means to then create reports which include sequence diagrams.

The `LsdContext` class can be used to capture the events to display on the sequence diagrams along with titles and 
descriptions for the various scenarios in the report.

## Properties
The following properties can be overridden by adding a properties file called `lsd.properties` on the classpath of your 
application.

| Property Name        | Default     | Description |
| ----------- | ----------- |------------ |
| lsd.core.label.maxWidth | 200 | The width in number of characters for the labels that appear on the diagrams before being abbreviated. |
| lsd.core.diagram.theme | plain | The plantUml theme to apply to the diagrams. See the [available themes](https://plantuml.com/theme). |
| lsd.core.report.outputDir | (temp directory) | The directory to write the report files. This can be a relative path e.g. `build/reports/lsd`.|

