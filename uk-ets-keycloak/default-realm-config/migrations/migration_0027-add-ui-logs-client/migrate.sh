# This migration performs the following steps:
# 1. Deletes and re-create the uk-ets-ui-logs-api Keycloak Client.
# 2. Creates realm-management client roles for the new service account user (this user was created automatically by step 1).
# 3. Creates uk-ets-ui-logs-api Audience Mappers for uk-ets-web-app

CLIENT_FILE_PATH=migrations/migration_0027-add-ui-logs-client/uk-ets-realm-ui-logs-api-client.json
INITIAL_CLIENT_FILE="$BASE_FILE_PATH/$CLIENT_FILE_PATH"
UPDATED_CLIENT_FILE=$BASE_FILE_PATH/migrations/temp_updated_client.json

# FAIL IF Default URL not found in environment
if [ "${UI_LOGS_BASE_URL:+set}" != set ]; then
  echo "UI_LOGS_BASE_URL environment variable has not been set"
  exit 2
fi
if
  ! sed -e "s/UI_LOGS_BASE_URL/$(echo $UI_LOGS_BASE_URL | sed 's/[^a-zA-Z 0-9]/\\&/g')/g" \
  "${INITIAL_CLIENT_FILE}" >"${UPDATED_CLIENT_FILE}"
then
  echo "Failed to update ${INITIAL_CLIENT_FILE} file with environment specific values"
  exit 2
fi
escapeJsonSpecialChars "$UPDATED_CLIENT_FILE"

#Delete uk-ets-ui-logs-api Client If it already exists
echo "Retrieving uk-ets-ui-logs-api id from UK ETS realm"

CLIENT_IDENTIFIER=$(getClientIdentifierByClientId "uk-ets-ui-logs-api")

if [ -n "$CLIENT_IDENTIFIER" ]; then
  # client exists, first delete it. NOTE: service account user and roles are also deleted
  echo "Deleting uk-ets-ui-logs-api client..."
  ACCESS_TOKEN=$(getAccessToken)
  HTTP_CODE=$(
    curl -s -XDELETE -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
    "${BASE_REALM_URL}/clients/${CLIENT_IDENTIFIER}" \
    --write-out "%{http_code}\n"
  )

  if [[ $HTTP_CODE -eq 204 ]]; then
    echo "UI Logs Client deleted"
  else
    echo "UI Logs was not deleted exiting..."
    exit 2
  fi
fi

## Create uk-ets-ui-logs-api client
echo "Creating UI Logs Client..."
ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPOST -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/clients" -d @"$UPDATED_CLIENT_FILE" --write-out "%{http_code}\n"
)

rm "$UPDATED_CLIENT_FILE"

if [[ $HTTP_CODE -eq 201 ]]; then
  echo "UI Logs Client created"
else
  echo "UI Logs Client was not created exiting..."
  exit 2
fi

# create realm-management client roles for service account user
echo "Retrieving Service Account user..."
NEW_UI_LOGS_CLIENT_IDENTIFIER=$(getClientIdentifierByClientId "uk-ets-ui-logs-api")
SERVICE_ACCOUNT_USER_IDENTIFIER=$(getSystemAccountUserIdByClientId "$NEW_UI_LOGS_CLIENT_IDENTIFIER")

echo "Setting Service Account client roles for uk-ets-ui-logs-api client..."
REALM_MANAGEMENT_CLIENT_IDENTIFIER=$(getClientIdentifierByClientId "realm-management")

HTTP_CODE=$(
  setClientRolesByUserIdAndClientId "$SERVICE_ACCOUNT_USER_IDENTIFIER" "$REALM_MANAGEMENT_CLIENT_IDENTIFIER" \
  "migration_0027-add-ui-logs-client/uk-ets-realm-ui-logs-realm-management-roles.json"
)

if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Realm-management client roles added to service account user"
else
  echo "Realm-management client roles were not added to service account user exiting..."
  exit 2
fi

####################################
## Audience Mapper for uk-ets-web-app
####################################
echo "Creating uk-ets-web-app Audience Mapper for uk-ets-ui-logs-api"

WEB_APP_CLIENT_IDENTIFIER=$(getClientIdentifierByClientId "uk-ets-web-app")
WEB_APP_CLIENT_MAPPERS_URL="${BASE_REALM_URL}/clients/${WEB_APP_CLIENT_IDENTIFIER}/protocol-mappers/models"
WEB_APP_CLIENT_MAPPERS_RELATIVE_PATH=migrations/migration_0027-add-ui-logs-client/uk-ets-client-uk-ets-web-app-mappers.json
WEB_APP_CLIENT_MAPPERS_CLIENT_ABSOLUTE_PATH="$BASE_FILE_PATH/$WEB_APP_CLIENT_MAPPERS_RELATIVE_PATH"


#read json file and get mapper .name field
AUDIENCE_MAPPER_NAME=$(jq .name "$WEB_APP_CLIENT_MAPPERS_CLIENT_ABSOLUTE_PATH")
AUDIENCE_MAPPER_NAME=$(sed -e 's/^"//' -e 's/"$//' <<<"$AUDIENCE_MAPPER_NAME")

#find mapper id by mapper name (if it exists)
AUDIENCE_MAPPER_IDENTIFIER=$(getMapperByClientIdAndMapperName "$WEB_APP_CLIENT_IDENTIFIER" "$AUDIENCE_MAPPER_NAME")

## Delete Audience Mapper by id if it exists
if [ -n "$AUDIENCE_MAPPER_IDENTIFIER" ]; then
  echo "Deleting Audience Mapper: '$AUDIENCE_MAPPER_NAME'..."
  HTTP_CODE=$(deleteMappersByClientIdAndMapperId "$WEB_APP_CLIENT_IDENTIFIER" "$AUDIENCE_MAPPER_IDENTIFIER")

  if [[ $HTTP_CODE -eq 204 ]]; then
    echo "Audience Mapper deleted"
  else
    echo "Audience Mapper was not deleted exiting..."
    exit 2
  fi
fi

# Create Audience Mapper for uk-ets-web-app
ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
curl -s -XPOST -H 'Accept: application/json' -H 'Content-Type: application/json' "$WEB_APP_CLIENT_MAPPERS_URL" \
-d @"$WEB_APP_CLIENT_MAPPERS_CLIENT_ABSOLUTE_PATH" \
-H "Accept: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" --write-out "%{http_code}\n")

if [[ $HTTP_CODE -eq 201 ]]; then
  echo "Audience Mapper uk-ets-web-app for uk-ets-ui-logs-api added successfully"
else
  echo "Error when adding uk-ets-web-app Audience Mapper for uk-ets-ui-logs-api. The process will exit"
  exit 2
fi