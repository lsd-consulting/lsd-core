[![semantic-release](https://img.shields.io/badge/semantic-release-e10079.svg?logo=semantic-release)](https://github.com/semantic-release/semantic-release)

# LSD Core
[![CI](https://github.com/lsd-consulting/lsd-core/actions/workflows/ci.yml/badge.svg)](https://github.com/lsd-consulting/lsd-core/actions/workflows/ci.yml)
[![Nightly Build](https://github.com/lsd-consulting/lsd-core/actions/workflows/nightly.yml/badge.svg)](https://github.com/lsd-consulting/lsd-core/actions/workflows/nightly.yml)
[![GitHub release](https://img.shields.io/github/release/lsd-consulting/lsd-core)](https://github.com/lsd-consulting/lsd-core/releases)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.lsd-consulting/lsd-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.lsd-consulting%22%20AND%20a:%22lsd-core%22)

## Usage
This library generates html reports and each report contains one or more scenarios that may have captured events that 
get displayed on sequence diagrams (a component diagram is generated too). 

* Use the LsdContext singleton instance to capture the events for each scenario to be included in the repost. This is a singleton instance
and can be accessed by calling the `getInstance()` static method:

```java
// Wherever required a reference to the lsdContext instance can be obtained like this
LsdContext.getInstance();
```

* Use the lsdContext to capture interactions during the runtime of the application or test e.g.
```java
// There are various types of events that can be captured, here are a couple examples using the provided builders
lsdContext.capture(ShortMessageInbound.builder().id(nextId()).to("A").label("in").data("start some job").build());
   
lsdContext.capture(Message.builder().id(nextId()).from("A").to("B").label("Message 1").data("some data 1").arrowType(BI_DIRECTIONAL).build());
```

* After the events have been captured for a particular scenario you should mark the scenario as complete and provide a name:
```java
lsdContext.completeScenario("A Scenario Title", "The sceenario description goes here and may contain html", SUCCESS);
```

* After one or more scenarios have been capture you can generate a report like so:
```java
// This returns a Path object for the generated file
lsdContext.completeReport("My Report Title")
```

**Additional options**
* Participants can be captured to provide aliases and control the appearance and order of the components on the sequence diagram e.g.
```java
lsdContext.addParticipants(List.of(
    ACTOR.called("A", "Arnie"),
    ACTOR.called("C"),
    DATABASE.called("B", "Barny\\nBoy"))
);
```

* To draw attention to some interesting details you can include a fact like so before the report is generated:
```java
// instances of the keyword Lorem will be highlighted on the report
lsdContext.addFact("Something to highlight", "Lorem");
```

## Framework Libraries

A few libraries exist to automate some of the steps to capture scenarios and generate reports e.g. via JUnit or Cucumber 
as plugins or extentions to the libraries.

| Name | Latest Version | Description |
| ----------- | ----------- |------------ |
| [lsd-junit5](https://github.com/lsd-consulting/lsd-junit5) | [![Maven Central](https://img.shields.io/maven-central/v/io.github.lsd-consulting/lsd-junit5.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.lsd-consulting%22%20AND%20a:%22lsd-junit5%22) | JUnit5 extension to generate LSD reports for unit tests |
| [lsd-cucumber](https://github.com/lsd-consulting/lsd-cucumber) |[![Maven Central](https://img.shields.io/maven-central/v/io.github.lsd-consulting/lsd-cucumber.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.lsd-consulting%22%20AND%20a:%22lsd-cucumber%22) | Cucumber plugin to generate LSD reports for specifications |

## Companion Libraries

Some libraries have been created to automate the capturing of events e.g. within SpringBoot applications

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
