# UK ETS User Registration API application

The UK ETS Registration User API is a Java(SpringBoot) application.

## Structure

## Running the application

You can run the Spring Boot application by typing:

    $ mvn clean spring-boot:run

You can then access the final jar file that contains both the API and the
UI code from here:

    uk-ets-registration-user-api/target

## Development

### Code quality

In order to enforce code quality the following plugins are used when building:

- Checkstyle using google_checks.xml (you can use this configuration
  from inside your IDE)
- PMD with the default configuration
- SpotBugs with the default configuration

If an issue is found then the build is failed.