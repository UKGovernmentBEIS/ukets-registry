# UK ETS Password Validator application

The UK ETS Password Validator is a Java(SpringBoot) based application.
This microservice is called from within Keycloak via a custom PasswordPolicyProvider.

## Required tools 
Please make sure the following tools are installed and properly configured.

- [Adopt OpenJDK 11 (LTS) HotSpot](https://adoptopenjdk.net/)
- [Apache Maven 3.6.2](https://maven.apache.org/download.cgi)
- [Docker Desktop](https://www.docker.com/products/docker-desktop) 

## Building the application
You can build the application by typing:

    $ mvn clean package

You can then access the final jar file that contains both the API and the
UI code from here:

    uk-ets-app-password-validator/target

## Running the application

You can run the Spring Boot application by typing:

    $ mvn clean spring-boot:run

## Running the supporting services
In order to setup the development environment and the supporting services
you need to have installed [Docker](https://www.docker.com/products/docker-desktop).

From the top directory type:

    $ docker-compose up -d

You will be able to access Keycloak [here](http://localhost:8091/auth/).