[![semantic-release](https://img.shields.io/badge/semantic-release-e10079.svg?logo=semantic-release)](https://github.com/semantic-release/semantic-release)
[![CI](https://github.com/lsd-consulting/lsd-core/actions/workflows/ci.yml/badge.svg)](https://github.com/lsd-consulting/lsd-core/actions/workflows/ci.yml)
[![Nightly Build](https://github.com/lsd-consulting/lsd-core/actions/workflows/nightly.yml/badge.svg)](https://github.com/lsd-consulting/lsd-core/actions/workflows/nightly.yml)
[![GitHub release](https://img.shields.io/github/release/lsd-consulting/lsd-core)](https://github.com/lsd-consulting/lsd-core/releases)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.lsd-consulting/lsd-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.lsd-consulting%22%20AND%20a:%22lsd-core%22)
[![](https://data.jsdelivr.com/v1/package/gh/lsd-consulting/lsd-core/badge)](https://www.jsdelivr.com/package/gh/lsd-consulting/lsd-core)

# LSD Core

A tool for creating sequence diagrams dynamically without needing to worry about markup.


This library generates html reports and each report contains one or more scenarios of captured events to be displayed 
on a sequence diagram.

(Additionally a component diagram is generated to show relationships). 

![LSD_example](https://user-images.githubusercontent.com/1330362/233956459-f8545861-b323-4243-9097-4b1dd1877bda.gif)

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


* User lsd context singleton to capture messages (and other events):
 
**Kotlin:**
  ```kotlin
  fun main() {
    val lsd = LsdContext.instance
  
    lsd.capture(
      MessageBuilder.messageBuilder().from("A").to("B").label("message1").build(),
      MessageBuilder.messageBuilder().from("B").to("A").label("message2").build(),
    )
    lsd.completeScenario("<Scenario Title>")
    lsd.completeReport("<Report Title>")
  }
  ```

**Java:**
```java
  public static void main(String[] args) {
      LsdContext lsd = LsdContext.getInstance();
      
      lsd.capture(
          MessageBuilder.messageBuilder().from("A").to("B").label("message1").build(),
          MessageBuilder.messageBuilder().from("B").to("A").label("message2").build()
      );
      lsd.completeScenario("<Scenario Title>", "<description>");
      lsd.completeReport("<Report Title>");
  }
```

---
#### Participants
Instead of using a String to specify a participant you can create a Participant type. This give you more options, 
e.g. to provide an alias , colour and type. 
So instead of `"A"` which will produce a default type of `PARTICIPANT` in the examples above you could use: 

  `ACTOR.called("A", "Arnie", "blue")` which will create a stickman labelled as "Arnie" and will be coloured in red.

`ParticipantType`s include:
* ACTOR
* BOUNDARY
* COLLECTIONS
* CONTROL
* DATABASE
* ENTITY
* PARTICIPANT
* QUEUE

You can define participants upfront and register them using the lsd context, e.g.

```kotlin
    lsd.addParticipants(listOf(arnie, donnie))
```
The participants specified here will override any other participants with the same name so if you want to ensure colours 
or aliases are not overridden set them here before creating a report.

---
#### SequenceEvents
There are other event types that can be captured, other than messages.

* **PageTitle**          - Sets a title on the diagram
* **NoteLeft**           - Create a note (can be to the left of a provided participant)
* **NoteRight**          - Similar to NoteLeft but on the right
* **NoteOver**           - Create a note in the middle of a participant lifeline
* **TimeDelay**          - Shows that a period of time has elapsed (optional label can be provided)
* **Newpage**            - Splits a diagram into a new page at the point this event was captured
* **ActivateLifeline**   - Activates a participant lifeline (colour can be provided)
* **DeactivateLifeline** - Deactivates a lifeline that has been activated

---

### Additional options
* A html index file can be generated if multiple reports are captured:
  ```kotlin
      lsd.createIndex()
  ```

* Generate a component diagram for events included from multiple scenarios and reports
  ```kotlin
      lsd.completeComponentsReport("Relationships")
  ```

* To draw attention to some interesting details you can include **facts** e.g.
  ```kotlin
      // instances of the keyword Lorem will be highlighted on the report
      lsd.addFact("Something to highlight", "Lorem")
  ```

* Advanced users may want to include **additional files** for additional icons etc. For example to include a heart icon on a note:
  ```kotlin
        lsd.includeFiles(listOf("tupadr3/font-awesome-5/heart"))

        lsd.capture(NoteLeft("Friends <$heart{scale=0.4,color=red}>"))
  ```
---
## Properties
The following properties can be overridden by adding a properties file called `lsd.properties` on the classpath of your 
application or by setting a System property. Note that System properties override file properties.

| Property Name        | Default           | Description                                                                                                                                                                                                                                                   |
| ----------- |-------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| lsd.core.label.maxWidth | 200               | The width in number of characters for the labels that appear on the diagrams before being abbreviated.                                                                                                                                                        |
| lsd.core.diagram.theme | plain             | The plantUml theme to apply to the diagrams. See the [available themes](https://plantuml.com/theme).                                                                                                                                                          |
| lsd.core.report.outputDir | build/reports/lsd | The directory to write the report files. (This can be a relative path).                                                                                                                                                                                       |
| lsd.core.ids.deterministic | false             | Determines how the html element ids are generated. Allowing deterministic ids is useful when testing (e.g. approval tests of html output since the generated ids won't be random. The default option which provides random ids should be preferred otherwise. |
| lsd.core.diagram.sequence.maxEventsPerDiagram | 50                | To help make really large diagrams easier to read this value is used to decide when to split a potentially large diagram into sub-diagrams. (Each sub diagram will remove any unused participants and include the participant headers and footers).           |

## Gallery

| Image                                                                                                        | Description                                                                                                                                                                                         |
|--------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| <img src="docs/example_index.png"  alt="index page example"/>                    | **Index pages** contain links to all reports that were generated prior to the index page being rendered                                                                                             |
| <img src="docs/combine_components_report.png"  alt="components report example"/> | **Components Reports** contain a component diagram of _all_ participants from all reports and scenarios that were captured prior to generating the components report via `completeComponentsReport()` |


## Related Libraries

A few [related libraries](https://github.com/lsd-consulting) exist to automate some of the steps to capture scenarios and generate reports e.g. via JUnit or Cucumber
as plugins or extentions to the libraries.


## Building

### Prerequisites
* Kotlin
* Java 11 JDK

### Git hooks

Git hooks will be configured automatically (to use the hooks in `.githooks` directory when the `gradle clean` task is invoked).

### Build

    ./gradlew clean build

