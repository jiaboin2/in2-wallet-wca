# Introduction 
The Wallet Creation Application (WCA) is a microservice responsible for implementing the necessary logic to
support the communication with issuers and validators, applying the OIDC4VCI and
OIDC4VP/SIOPv2 standards.

# Main Features

## Issuing of verifiable credentials - OIDC4VCI Protocol
1. Get the Credential Offer
2. Get the Credential Issuer Metadata
3. Request/Get for an Access Token
4. Request/Get a Verifiable Credential
5. Store it into the wallet-data

## Authentication with Verifiable Credentials - OID4VP/SIOPv2 Protocol
1. Get/Verify Authorization Request
2. Create Verifiable Presentation
3. Create/Send Authorization Response 

# Build and Test
We have 2 different ways to build and test the project depending on the selected Spring Boot profile.
- `test` profile: This profile is used for unit testing. It uses an in-memory database and does not require any external dependencies.
- `local` profile: This profile is used for local development. It uses an in-memory database and generates default data to test the application. 
- `local-docker` profile: This profile is used for local development. It uses a dockerized database and generates default data to test the application.

# Documentation

- Swagger: http://localhost:8083/swagger-ui.html
- OpenAPI: http://localhost:8083/api-docs

# Getting Started
## Prerequisites

- Java 17
- Gradle
- Spring Boot
- Docker Desktop
- IntelliJ IDEA
- Git