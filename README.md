# LSD Core

[![Build](https://github.com/nickmcdowall/lsd-core/actions/workflows/macos-build.yml/badge.svg)](https://github.com/nickmcdowall/lsd-core/actions/workflows/macos-build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.nickmcdowall/lsd-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.nickmcdowall%22%20AND%20a:%22lsd-core%22)

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
| lsd.core.ids.deterministic | false | Determines how the html element ids are generated. Allowing deterministic ids is useful when testing (e.g. approval tests of html output since the generated ids won't be random. The default option which provides random ids should be preferred otherwise.|

## Building

### Prerequisites

* Graphviz is used to generate the component diagrams. Without having this installed the approval tests will fail because 
the generated html file will not match the expected output. See [graphviz.org/download/](https://graphviz.org/download/) 
for installation instructions.

* Java 11 
* IDE's will need to enable annotation processing (Lombok is used throughout the project to reduce the amount of boilerplate code).

### Build

    ./gradlew clean build