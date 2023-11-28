# UK ETS Database Change Management

## A. Database Setup

#### 1. Navigate to the directory of the Developer's Environment project

    $ cd uk-ets-env-development

#### 2. Generate the registry application database instance

    $ docker-compose up -d postgres-registry

## B. Automatic execution of Liquibase

The liquibase scripts are configured to be executed automatically upon deployment of the API (uk-ets-registry-api).

To disable the automatic execution of Liquibase, set the following property in `application.properties`:

- `spring.liquibase.enabled = false`

## C. Manually executing Liquibase

#### 1. Verify connection properties

Make sure the connection properties to database are correct, by checking the content of this file:

- `src/main/resources/liquibase/registry/liquibase.properties`

Content:

```
driver: org.postgresql.Driver
url: jdbc:postgresql://localhost/registry
username: registry
password: ********
verbose: true
```

#### 2. Navigate to the directory of this project

    $ cd uk-ets-db

#### 3. Check the current database status

    $ mvn clean package liquibase:status -Pregistry

Sample output:

```
[INFO] SELECT COUNT(*) FROM databasechangelog
11 change sets have not been applied to keycloak@jdbc:postgresql://localhost/ukets
     changelogs/v0.2.0/changelog_v0_2_0.xml::0_2_0_TABLE_CONTACT::pougouniasn
     changelogs/v0.2.0/changelog_v0_2_0.xml::0_2_0_TABLE_USER::pougouniasn
     changelogs/v0.2.0/changelog_v0_2_0.xml::0_2_0_TABLE_ACCOUNT_HOLDER::pougouniasn
     changelogs/v0.2.0/changelog_v0_2_0.xml::0_2_0_TABLE_ROLE::pougouniasn
     changelogs/v0.2.0/changelog_v0_2_0.xml::0_2_0_TABLE_AIRCRAFT_OPERATOR::pougouniasn
```

#### 4. Update the database

    $ mvn clean package liquibase:update -Pregistry -D"runtime-user=registry"

Sample output:

```
[INFO] Successfully released change log lock
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.885 s
[INFO] Finished at: 2019-10-22T16:10:11+03:00
[INFO] ------------------------------------------------------------------------
```
