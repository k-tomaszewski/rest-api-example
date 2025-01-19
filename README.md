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
- Java 21
- Latest stable Spring Boot
- Maven
- Embedded database...
- TBC...
