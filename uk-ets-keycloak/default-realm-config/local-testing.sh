#!/bin/bash
# this script should be used for testing only in local environments.
# running this script you can test migrations without having to rebuild and re-deploy keycloak on every change.

URL="localhost:8091"
REALM_NAME="uk-ets"
BASE_REALM_URL="http://$URL/auth/admin/realms/$REALM_NAME"
BASE_FILE_PATH="."
KEYCLOAK_ADMIN="admin"
KEYCLOAK_ADMIN_PASSWORD="Pa55w0rd"
KEYCLOAK_MAIL_HOST='mailhog'
KEYCLOAK_MAIL_PORT="25"
KEYCLOAK_MAIL_FROM="t\est@test.com" #test for backslash!
PASSWORD_VALIDATOR="uk-ets-app-password-validator"
UI_LOGS_BASE_URL="http://localhost:9097/api-ui-logs"
PUBLICATION_API_BASE_URL="http://localhost:9099/api-publication"
# shellcheck source=./migrations/perform-migrations.lib"
source "$(dirname "$0")/migrations/perform-migrations.lib"

performMigrations
