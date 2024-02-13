# Alpaca - What is this?

Alpaca is a small desktop application that collects information about association members. In a way, it's a simple customer relationship management system, but instead of customers, it collects information about membership status and personal data. 
Note: the development is still in progress. We want to add support for https://en.wikipedia.org/wiki/Double-entry_bookkeeping so that Alpaca could also be accounting application.

## Technology Stack 
* Java version: 17
* GUI: JavaFX
* Spring Boot, Hibernate
* Database: any SQL db supported by Hibernate (currently it is using H2 with storing data on disk: https://www.baeldung.com/h2-embedded-db-data-storage)

## Features
* browsing, creating and editing of person data
* attaching and storing of related docs (PDF, DOC, graphic images)
* importing from a legacy DBF database
* exporting to CSV
* validation of bank accounts based NBP (Polish National Bank)

## JavaFX topics
* Sorting and pagination: http://incepttechnologies.blogspot.com/p/javafx-tableview-with-pagination-and.html
* Sorting and filtering: https://code.makery.ch/blog/javafx-8-tableview-sorting-filtering/
* MVC patterns with JavaFX: https://stackoverflow.com/questions/32342864/applying-mvc-with-javafx

## Docs/libs used during development
 
### Integration between Spring Boot and JavaFX
* https://blog.jetbrains.com/idea/2019/11/tutorial-reactive-spring-boot-a-javafx-spring-boot-application/
* FXWeaver https://rgielen.net/posts/2019/introducing-fxweaver-dependency-injection-support-for-javafx-and-fxml/ 
### Migration to Java 17
* https://blog.codecentric.de/migrating-spring-boot-java-17
### Event Driven Architecture
* https://www.baeldung.com/spring-events
### Content Repository
* https://paulcwarren.github.io/spring-content/spring-content-fs-docs/

## Technical Details

### Importing DBF files
NOTE: Characters encoding is in Charset.forName("Cp1250")

