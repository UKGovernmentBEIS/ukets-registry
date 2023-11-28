## Custom rest endpoint for searching users.

#### Examples

http://localhost:8091/auth/realms/uk-ets/uk-ets-users

http://localhost:8091/auth/realms/uk-ets/uk-ets-users/contacts?urid=UK802061511788&urid=UK570215346642&urid=UK758296034143

#### Note about JPA entities
All Keycloak DB schema JPA Entities are used to generate the required artifacts for queryDSL.
This means that we depend heavily on the keycloak schema.
In case a new entity is added you have to modify the package-info.java in the gov.uk.ets.keycloak.users.service.infrastructure package
and the persistence.xml under the src\test\resources directory of the uk-ets-keycloak-users-service module for the project to compile.