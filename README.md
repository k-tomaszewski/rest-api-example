# REST API example with Java and Spring Boot
This is a technology demonstration project to present how to make a simple REST API service with Java 21 and Spring Boot.

## Requirements
The example was designed assuming following functional and non-functional requirements.

### Functional requirements
1. REST API must provide an endpoint to create an account
2. Account creation input data must contain: initial account balance in PLN, first name, last name.
3. Account creation procedure generates unique account identifier, which is returned and must be used in subsequent REST API calls.
4. REST API must provide an endpoint for a currency exchange between PLN and USD.
5. Exchange rates must be fetched from a public REST API provided by NBP: http://api.nbp.pl
6. REST API must provide an endpoint for fetching account data including balance in PLN and USD.

### Non-functional requirements
1. Implementation in Java.
2. Data are persisted.
3. Open sourced.
4. Using Maven or Gradle.
5. Instruction how to run provided in README file.

## Design decisions
My design decisions and technology choice are:
- Java 21, as this is the current LTS Java version. 
- Latest stable Spring Boot
- Maven - rational given here: https://artofcode.wordpress.com/2017/11/17/gradle-vs-maven/
- Embedded database was selected as it is simpler then running an external database server. H2 database was selected as SQL embedded 
databases has a very good Spring Boot support. Embedded H2 database can persist data on disk, thus it is fulfilling above requirements.
- Java Bean Validation used for validation as this is supported very well by Spring Boot.
- HTTPS will be used to communicate with NBP API instead of HTTP as it is available and it's better suited for fetching critical data:
https://api.nbp.pl/

## Instructions

### How to build?
Execute `mvn clean verify` to build the application. As a result a JAR file will be created in 
the `target` subdirectory of project's root directory.

### How to run with database persisted on disk?
1. Set the environment variable "SPRING_DATASOURCE_URL" defining a directory for database files and a database name. The last part of the path below is a database name ("h2-db").
```bash
export SPRING_DATASOURCE_URL=jdbc:h2:/some/path/h2-db
```
2. Run JAR file with the application:
```bash
java -jar target/fx-service-example-0.0.1-SNAPSHOT.jar
```
By default the application starts listening on TCP port 8080.
