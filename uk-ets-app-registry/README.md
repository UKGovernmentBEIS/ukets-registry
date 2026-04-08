# UK ETS Registry application

The UK ETS Registry is a Java(SpringBoot) / Angular based application.

## Required tools 
Please make sure the following tools are installed and properly configured.

- [Adopt OpenJDK >=21 (LTS) HotSpot](https://adoptopenjdk.net/)
- [Apache Maven](https://maven.apache.org/download.cgi)
- [Docker Desktop](https://www.docker.com/products/docker-desktop) 
- [NodeJS](https://nodejs.org/en/)
- [Angular CLI](https://cli.angular.io/)

## Structure

## Web application
Please refer [here](uk-ets-registry-web/README.md) for information on the
web application development and build instructions.

## API application (backend)
Please refer [here](uk-ets-registry-api/README.md) for information on the
web application development and build instructions.

## Running the supporting services
In order to setup the development environment and the supporting services
you need to have installed [Docker](https://www.docker.com/products/docker-desktop).

From the top directory type:

    $ docker-compose up -d

You will be able to access Keycloak [here](http://localhost:8091/auth/).

## Building the application
You can build the application by typing:

    $ mvn clean package

You can then access the final jar file that contains both the API and the
UI code from here:

    uk-ets-registry-api/target

