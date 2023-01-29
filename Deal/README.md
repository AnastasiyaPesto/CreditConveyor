# Microservice "Deal"

This is implementation microservice "Deal" which is part of application "Credit conveyor"

**Used technologies:**
Java 11, Spring Boot 2.7.6, Open API, Swagger, JUnit 5, Mockito, Liquibase

### Database

Database tables are created by Liquibase scripts (*.yaml)

### API endpoints

**POST**

`/deal/application`

**PUT**

`/deal/offer`

`/deal/calculate/{applicationId}`
