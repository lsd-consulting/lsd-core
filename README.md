[![semantic-release](https://img.shields.io/badge/semantic-release-e10079.svg?logo=semantic-release)](https://github.com/semantic-release/semantic-release)
[![CI](https://github.com/lsd-consulting/lsd-core/actions/workflows/ci.yml/badge.svg)](https://github.com/lsd-consulting/lsd-core/actions/workflows/ci.yml)
[![Nightly Build](https://github.com/lsd-consulting/lsd-core/actions/workflows/nightly.yml/badge.svg)](https://github.com/lsd-consulting/lsd-core/actions/workflows/nightly.yml)
[![GitHub release](https://img.shields.io/github/release/lsd-consulting/lsd-core)](https://github.com/lsd-consulting/lsd-core/releases)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.lsd-consulting/lsd-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.lsd-consulting%22%20AND%20a:%22lsd-core%22)

# LSD Core

A tool for creating sequence diagrams dynamically without needing to worry about markup.

![example_sequence_diagram.png](docs%2Fexample_sequence_diagram.png)

This library generates html reports and each report contains one or more scenarios of captured events to be displayed 
 on a sequence diagram.

(Additionally a component diagram is generated to show relationships). 

![example_component_diagram.png](docs%2Fexample_component_diagram.png)

## Usage

* Add dependency for version: [![Maven Central](https://img.shields.io/maven-central/v/io.github.lsd-consulting/lsd-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.lsd-consulting%22%20AND%20a:%22lsd-core%22)

    <details>
      <summary>Maven</summary>
    
    ```xml
      <dependency>
          <groupId>io.github.lsd-consulting</groupId>
          <artifactId>lsd-core</artifactId>
          <version>X.X.X</version>
      </dependency>
    ```
    
    </details>
    
    <details>
      <summary>Gradle</summary>
    
    ```groovy
        implementation 'io.github.lsd-consulting:lsd-core:X.X.X'
    ```
    </details>


* Use the LsdContext singleton instance to capture the events for each scenario to be included in the report. 

    <details>
    <summary>Java example:</summary>

    ```java
        import static com.lsd.core.builders.MessageBuilder.*;
    
        ...
                
        // A reference to the lsdContext instance can be obtained like this
        var lsdContext = LsdContext.getInstance();
        
        ...
    
        var arnie = ACTOR.called("Arnie");
        var bank = PARTICIPANT.called("Bank");
        var repository = DATABASE.called("Repository");
    
        lsdContext.addParticipants(List.of(arnie, bank, repository));
        
        lsdContext.capture(new PageTitle("Checking account balanace"));
        lsdContext.capture(new NoteLeft("On payday", arnie));
    
        lsdContext.capture(messageBuilder().id(nextId())
            .from(arnie)
            .to(bank)
            .label("What is my balance?")
            .data("{ name: 'arnie' }").build());
    
        lsdContext.capture(new NoteLeft("High load on\\n payday", bank));
    
        lsdContext.capture(messageBuilder().id(nextId())
            .from(bank)
            .to(repository)
            .label("Get balance for Arnie").build());
    
        lsdContext.capture(new TimeDelay("a couple seconds later"));
    
        lsdContext.capture(messageBuilder().id(nextId())
            .from(repository)
            .to(bank)
            .type(SYNCHRONOUS_RESPONSE)
            .label("Nothing yet..").build());
    
        lsdContext.capture(messageBuilder().id(nextId())
            .from(bank)
            .to(arnie)
            .label("Your balance is 0")
            .type(SYNCHRONOUS_RESPONSE).build());
    
        lsdContext.completeScenario("Checking bank balance", "Capture bank balance lookup", SUCCESS);
    
        lsdContext.completeReport("Bank balance interactions");
    ```
    
    </details>

### Additional options
* A html index file can be generated if multiple reports are captured:
  ```java
      lsdContext.createIndex()
  ```

* Generate a component diagram for events included from multiple scenarios and reports
  ```java
      lsdContext.completeComponentsReport("Relationships")
  ```

* To draw attention to some interesting details you can include **facts** e.g.
  ```java
      // instances of the keyword Lorem will be highlighted on the report
      lsdContext.addFact("Something to highlight", "Lorem");
  ```

* Advanced users may want to include **additional files** for additional icons etc. For example to include a heart icon on a note:
  ```java
        lsdContext.includeFiles(List.of("tupadr3/font-awesome-5/heart"));

        lsdContext.capture(new NoteLeft("Friends <$heart{scale=0.4,color=red}>", null));
  ```
  
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


## Related Libraries

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


## Building

### Prerequisites
* Kotlin
* Java 11 JDK

### Git hooks

Git hooks will be configured automatically (to use the hooks in `.githooks` directory when the `gradle clean` task is invoked).

### Build

    ./gradlew clean build

