# LSD Core

[![Build](https://github.com/nickmcdowall/lsd-core/actions/workflows/macos-build.yml/badge.svg)](https://github.com/nickmcdowall/lsd-core/actions/workflows/macos-build.yml)

This library can be used to create reports which include sequence diagrams.

The idea is to auto or semi-auto generate sequence diagrams from component tests or end-to-end
tests so that a form of living documentation can be created.

## Properties
The following properties can be overridden by adding a properties file called `lsd.properties` in your 
application.

| Name        | Default     | Description |
| ----------- | ----------- |------------ |
| lsd.core.label.max-width | 200 | The width in number of characters for the labels that appear on the arrows for the sequence diagrams before being abbreviated. |
| lsd.core.diagram.theme | plain | The plantUml theme to apply to the diagrams. See the [available themes](https://plantuml.com/theme). |

