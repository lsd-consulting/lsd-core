[![semantic-release](https://img.shields.io/badge/semantic-release-e10079.svg?logo=semantic-release)](https://github.com/semantic-release/semantic-release)

# LSD Core
[![CI](https://github.com/lsd-consulting/lsd-core/actions/workflows/ci.yml/badge.svg)](https://github.com/lsd-consulting/lsd-core/actions/workflows/ci.yml)
[![Publish](https://github.com/lsd-consulting/lsd-core/actions/workflows/publish.yml/badge.svg)](https://github.com/lsd-consulting/lsd-core/actions/workflows/publish.yml)
[![Nightly Build](https://github.com/lsd-consulting/lsd-core/actions/workflows/nightly.yml/badge.svg)](https://github.com/lsd-consulting/lsd-core/actions/workflows/nightly.yml)
[![GitHub release](https://img.shields.io/github/release/lsd-consulting/lsd-core)](https://github.com/lsd-consulting/lsd-core/releases)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.lsd-consulting/lsd-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.lsd-consulting%22%20AND%20a:%22lsd-core%22)

## Usage
This core library is used by higher level libraries to create html reports containing sequence diagrams.

The core domain objects and reporting output is contained within this library but the higher level libraries are responsible for
orchestrating and capturing the data that gets turned into reports.

See below framework libraries for example libraries that make use of this core library.

## Framework Libraries
| Name | Latest Version | Description |
| ----------- | ----------- |------------ |
| [lsd-junit5](https://github.com/lsd-consulting/lsd-junit5) | [![Maven Central](https://img.shields.io/maven-central/v/io.github.lsd-consulting/lsd-junit5.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.lsd-consulting%22%20AND%20a:%22lsd-junit5%22) | JUnit5 extension to generate LSD reports for unit tests |
| [lsd-cucumber](https://github.com/lsd-consulting/lsd-cucumber) |[![Maven Central](https://img.shields.io/maven-central/v/io.github.lsd-consulting/lsd-cucumber.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.lsd-consulting%22%20AND%20a:%22lsd-cucumber%22) | Cucumber plugin to generate LSD reports for specifications |

## Companion Libraries
| Name | Latest Version | Description |
| ----------- | ----------- |------------ |
| [lsd-interceptors](https://github.com/lsd-consulting/lsd-interceptors) | [![Maven Central](https://img.shields.io/maven-central/v/io.github.lsd-consulting/lsd-interceptors.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.lsd-consulting%22%20AND%20a:%22lsd-interceptors%22) | Automates the collection of HTTP requests and AMQP messages within a springboot microservice for the sequence diagrams. Works well for acceptance or component tests. |
| [lsd-distributed-interceptors](https://github.com/lsd-consulting/lsd-distributed-interceptors) |![Maven Central](https://img.shields.io/maven-central/v/io.github.lsd-consulting/lsd-distributed-interceptor) | Enables the automated collection of HTTP requests and AMQP messages sent between springboot microservices. Works well for end to end tests or general purpose tracing of all events being sent between services |

## Properties
The following properties can be overridden by adding a properties file called `lsd.properties` on the classpath of your 
application or by setting a System property. Note that System properties override file properties.

| Property Name        | Default     | Description |
| ----------- | ----------- |------------ |
| lsd.core.label.maxWidth | 200 | The width in number of characters for the labels that appear on the diagrams before being abbreviated. |
| lsd.core.diagram.theme | plain | The plantUml theme to apply to the diagrams. See the [available themes](https://plantuml.com/theme). |
| lsd.core.report.outputDir | build/reports/lsd | The directory to write the report files. (This can be a relative path).|
| lsd.core.ids.deterministic | false | Determines how the html element ids are generated. Allowing deterministic ids is useful when testing (e.g. approval tests of html output since the generated ids won't be random. The default option which provides random ids should be preferred otherwise.|
| lsd.core.diagram.sequence.maxEventsPerDiagram | 50 | To help make really large diagrams easier to read this value is used to decide when to split a potentially large diagram into sub-diagrams. (Each sub diagram will remove any unused participants and include the participant headers and footers). |

## Building

### Prerequisites
* Java 11 
* IDE's will need to enable annotation processing (Lombok is used throughout the project to reduce the amount of boilerplate code).

### Git hooks

Git hooks will be configured automatically (to use the hooks in `.githooks` directory when the `gradle clean` task is invoked).

### Build

    ./gradlew clean build
