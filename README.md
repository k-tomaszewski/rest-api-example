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

Everything not specified can be designed and implemented in any desired way.

## Design decisions
My design decisions and technology choice are:
- Java 21, as this is the current LTS Java version. 
- Latest stable Spring Boot
- Maven - rational given here: https://artofcode.wordpress.com/2017/11/17/gradle-vs-maven/
- Embedded database was selected as it is simpler then running an external database server. H2 database was selected as SQL embedded 
databases has a very good Spring Boot support. Embedded H2 database (http://www.h2database.com/html/main.html) can persist data on disk, thus it is fulfilling above requirements.
- Java Bean Validation used for validation as this is supported very well by Spring Boot.
- HTTPS will be used to communicate with NBP API instead of HTTP as it is available and it's better suited for fetching critical data:
https://api.nbp.pl/
- Negative HTTP response bodies have format defined by RFC-9457.

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

If the above-mentioned environment variable is not set, the application defaults to start the
embedded database in in-memory mode.

## REST API guide
There are 3 REST API endpoints supported to meet above-mentioned requirements:
1. POST /accounts
2. GET  /accounts/{account-id}
3. POST /accounts/{account-id}/fx-transactions


### How to create an account?
Execute following command when the application is running to create an account:
```bash
curl -X POST http://localhost:8080/accounts -H "Content-Type: application/json" -d '{"firstName": "Bolek", "lastName": "Nowy", "plnBalance": "1000.23"}' 
```
The response contains full account details, including account ID:
```json
{"id":1,"firstName":"Bolek","lastName":"Nowy","balance":{"PLN":1000.23,"USD":0.00}}
```
### How to get account details?
Assuming account ID is 1, use following command to fetch account details:
```bash
curl http://localhost:8080/accounts/1
```
### How to exchange currency?
#### How to exchange given amount of a given currency?
Assuming you want to operate on account with ID 1, use following command to exchange 100.25 PLN to USD:
```bash
curl -X POST http://localhost:8080/accounts/1/fx-transactions -H "Content-Type: application/json" -d '{"srcCcy": "PLN", "srcAmount": "100.25", "dstCcy": "USD"}'
```
The operation returns a summary of an exchange if it was successful (HTTP 200):
```json
{"srcCcy":"PLN","srcAmount":100.25,"dstCcy":"USD","dstAmount":24.02,"price":4.1740,"accountBalance":{"PLN":899.98,"USD":24.02}}
```
Attributes _"srcAmount"_ and _"srcCcy"_ inform about money that was exchanged.
Attributes _"dstAmount"_ and _"dstCcy"_ inform what was exchange result.
The attribute _"price"_ contains price that was used in exchange.
The attribute _"accountBalance"_ contains account balance after the exchange operation.

### How to change to obtain a given amount of a given currency?
In this mode the request body must contain _"dstAmount"_ instead of _"srcAmount"_ attribute:
```bash
curl -X POST http://localhost:8080/accounts/1/fx-transactions -H "Content-Type: application/json" -d '{"srcCcy": "PLN", "dstCcy": "USD", "dstAmount": "1.00"}'
```
