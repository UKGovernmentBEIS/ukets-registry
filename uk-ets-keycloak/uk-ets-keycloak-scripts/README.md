# UK ETS Keycloak scripts

Contains javascript based policies for the uk-ets-registry-api authorization services.
Specifically it contains three policies named:
1. Registered User Policy
1. Validated User Policy
1. Enrolled User Policy

See [JavaScript-Based Policy](https://www.keycloak.org/docs/latest/authorization_services/#_policy_js) in keycloak documentation on how to 
define these policies using the evaluation API.

Since Keycloak v8.0.0 uploading of javascript is disabled by default, so these scripts have to be deployed as a jar,see the 
[Keycloak Server Development](https://www.keycloak.org/docs/latest/server_development/#_script_providers).