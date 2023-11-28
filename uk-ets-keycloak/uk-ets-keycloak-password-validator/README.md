# UK ETS Keycloak Password validator

A custom REST endpoint used for validating a password for an authenticated user.

Obtaining an OAuth2 Access Token
----------------------------------------

First thing, your client needs to obtain an OAuth2 access token from a Keycloak server for a user.
This can be done for example from the project's Postman collection using the pre-request script. 
The problem is that if the user has OTP enabled you must enter the OTP code manually.

Validating the password
----------------------------------------

You can invoke the endpoint by POSTING the password in a PasswordValidationRequest , with a bearer token in the header at the following url http://localhost:8091/auth/realms/uk-ets/password-validator 

See also the [Keycloak Server Developer Guide](https://www.keycloak.org/docs/latest/server_development/index.html#_extensions_rest).
