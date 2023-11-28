# UK ETS Keycloak OTP validator

A custom REST endpoint used for validating OTP for an authenticated user.

Obtaining an OAuth2 Access Token
----------------------------------------

First thing, your client needs to obtain an OAuth2 access token from a Keycloak server for a user  who has setup 2FA. 
You can do this by passing an extra parameter otp in the request.Have a look at the POSTMAN collection under src/test/resources/postman.

Validating an OTP
----------------------------------------

You can invoke the endpoint by POSTING the otp as a form parameter , with a bearer token in the header at the following url http://localhost:8091/auth/realms/uk-ets/otp-validator 

See also the [Keycloak Server Developer Guide](https://www.keycloak.org/docs/latest/server_development/index.html#_extensions_rest).