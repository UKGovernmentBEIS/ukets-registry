#!/bin/bash
# this way we can invoke functions defined in separate shell scripts:
# shellcheck source=./migrations/perform-migrations.lib"
source "$(dirname "$0")/migrations/perform-migrations.lib"

URL="localhost:8080"

if [ -z "$BASE_URL" ]; then
  echo "BASE_URL is empty proceeding..."
elif [ "$BASE_URL" = "http://localhost:4200" ]; then
  echo "BASE_URL: $BASE_URL proceeding with development environment"
else
  echo "BASE_URL: $BASE_URL proceeding with environment configuration"
  ESCAPED_BASE_URL_1=`echo ${BASE_URL/\:/\\\:}`
  ESCAPED_BASE_URL_2=`echo ${ESCAPED_BASE_URL_1//\//\\\/}`
  sed -i "s/http\:\/\/localhost\:4200/$ESCAPED_BASE_URL_2/g" /opt/keycloak/tools/uk-ets-realm-base.json
  sed -i "s/http\:\/\/localhost\:8080/$ESCAPED_BASE_URL_2/g" /opt/keycloak/tools/uk-ets-realm-base.json
fi

KEYCLOAK_STATUS=`curl -sL "http://$URL/auth/" | grep -Po 'Welcome to Keycloak'`
if [[ "$?" -eq 0 ]]; then
  echo "Keycloak is Up and Running"
  REALM_NAME=`cat /opt/keycloak/tools/uk-ets-realm-base.json | jq -r '.realm'`
  REALM_STATUS=`curl -sL "http://$URL/auth/realms/$REALM_NAME/" | jq -r '.realm' | grep $REALM_NAME`
  if [[ "$?" -ne 0 ]]; then
    ACCESS_TOKEN=`curl -sL -H "application/x-www-form-urlencoded" -d "grant_type"="password" -d "client_id"="admin-cli" -d "username"="$KEYCLOAK_ADMIN" -d "password"="$KEYCLOAK_ADMIN_PASSWORD" "http://$URL/auth/realms/master/protocol/openid-connect/token" | jq -r '.access_token'`
    #echo $ACCESS_TOKEN
    echo "Importing Base configuration for uk-ets Realm"
    curl -XPOST -H 'Accept: application/json' -H "Content-Type: application/json" -H 'cache-control: no-cache' -H "Authorization: Bearer $ACCESS_TOKEN" "http://$URL/auth/admin/realms" -d @/opt/keycloak/tools/uk-ets-realm-base.json
    REALM_STATUS=`curl -sL "http://$URL/auth/realms/$REALM_NAME/" | jq -r '.realm' | grep $REALM_NAME`
    if [[ "$REALM_STATUS" -eq 0 ]]; then

      echo "Base config was created successfully, let's proceed importing users"

      if [[ $CREATE_ADMIN_USERS == true ]]; then
        INITIAL_USERS_FILE=/opt/keycloak/tools/uk-ets-realm-dev-sample-users.json
      else
        INITIAL_USERS_FILE=/opt/keycloak/tools/uk-ets-realm-prod-sample-users.json
      fi

      TEMP_USERS_FILE="$BASE_FILE_PATH"/uk-ets-sample-users_updated.json
      parseUserFile "$TEMP_USERS_FILE" "$INITIAL_USERS_FILE"
      USERS_FILE=$TEMP_USERS_FILE

      ACCESS_TOKEN=$(getAccessToken)
      curl -s -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
      "http://$URL/auth/admin/realms/$REALM_NAME/partialImport" -d @"${USERS_FILE}"

      # remove the temporary file after the request
      rm "$TEMP_USERS_FILE"

      echo "Sample Users were created successfully, lets proceed"
    else
      echo "Base Config was not imported successfully, exiting..."
      exit 2
    fi
  else
    echo "Realm exists"
  fi
else
  echo "ERROR! Keycloak is not Running, please verify status..."
  exit 2
fi

performMigrations
