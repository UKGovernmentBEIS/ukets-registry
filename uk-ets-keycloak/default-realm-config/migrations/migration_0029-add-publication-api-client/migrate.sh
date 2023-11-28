# This migration performs the following steps:
# 1. Deletes and re-create the uk-ets-publication-api Keycloak Client.
# 2. Creates realm-management client roles for the new service account user (this user was created automatically by step 1).
# 3. Creates the client role site-publisher for the new uk-ets-publication-api client.
# 4. Adds the new role to all existing senior and junior administrators and authority users.

CLIENT_FILE_PATH=migrations/migration_0029-add-publication-api-client/uk-ets-realm-publication-api-client.json
INITIAL_CLIENT_FILE="$BASE_FILE_PATH/$CLIENT_FILE_PATH"
UPDATED_CLIENT_FILE=$BASE_FILE_PATH/migrations/temp_updated_client.json

# FAIL IF Default URL not found in environment
if [ "${PUBLICATION_API_BASE_URL:+set}" != set ]; then
  echo "PUBLICATION_API_BASE_URL environment variable has not been set"
  exit 2
fi
if
  ! sed -e "s/PUBLICATION_API_BASE_URL/$(echo $PUBLICATION_API_BASE_URL | sed 's/[^a-zA-Z 0-9]/\\&/g')/g" \
  "${INITIAL_CLIENT_FILE}" >"${UPDATED_CLIENT_FILE}"
then
  echo "Failed to update ${INITIAL_CLIENT_FILE} file with environment specific values"
  exit 2
fi
escapeJsonSpecialChars "$UPDATED_CLIENT_FILE"

#Delete uk-ets-publication-api Client If it already exists
echo "Retrieving uk-ets-publication-api id from UK ETS realm"

CLIENT_IDENTIFIER=$(getClientIdentifierByClientId "uk-ets-publication-api")

if [ -n "$CLIENT_IDENTIFIER" ]; then
  # client exists, first delete it. NOTE: service account user and roles are also deleted
  echo "Deleting uk-ets-publication-api client..."
  ACCESS_TOKEN=$(getAccessToken)
  HTTP_CODE=$(
    curl -s -XDELETE -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
    "${BASE_REALM_URL}/clients/${CLIENT_IDENTIFIER}" \
    --write-out "%{http_code}\n"
  )

  if [[ $HTTP_CODE -eq 204 ]]; then
    echo "Publication api Client deleted"
  else
    echo "Publication api was not deleted exiting..."
    exit 2
  fi
fi

## Create uk-ets-publication-api client
echo "Creating Publication api Client..."
ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPOST -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/clients" -d @"$UPDATED_CLIENT_FILE" --write-out "%{http_code}\n"
)

rm "$UPDATED_CLIENT_FILE"

if [[ $HTTP_CODE -eq 201 ]]; then
  echo "Publication api Client created"
else
  echo "Publication api Client was not created exiting..."
  exit 2
fi

# create realm-management client roles for service account user
echo "Retrieving Service Account user..."
NEW_PUBLICATION_CLIENT_IDENTIFIER=$(getClientIdentifierByClientId "uk-ets-publication-api")
SERVICE_ACCOUNT_USER_IDENTIFIER=$(getSystemAccountUserIdByClientId "$NEW_PUBLICATION_CLIENT_IDENTIFIER")

echo "Setting Service Account client roles for uk-ets-publication-api client..."
REALM_MANAGEMENT_CLIENT_IDENTIFIER=$(getClientIdentifierByClientId "realm-management")

HTTP_CODE=$(
  setClientRolesByUserIdAndClientId "$SERVICE_ACCOUNT_USER_IDENTIFIER" "$REALM_MANAGEMENT_CLIENT_IDENTIFIER" \
  "migration_0029-add-publication-api-client/uk-ets-realm-publication-api-realm-management-roles.json"
)

if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Realm-management client roles added to service account user"
else
  echo "Realm-management client roles were not added to service account user exiting..."
  exit 2
fi

# create new client role 'site-publisher'
echo "Adding new role for client uk-ets-publication-api..."

HTTP_CODE=$(addRoleToClient "$NEW_PUBLICATION_CLIENT_IDENTIFIER" "site-publisher")

if [[ $HTTP_CODE -eq 201 ]]; then
  echo "New role for client added"
else
  echo "Role was not added to client exiting..."
  exit 2
fi

# adding new role to all existing administrators
ROLE_ID=$(getClientRoleByName "$NEW_PUBLICATION_CLIENT_IDENTIFIER" "site-publisher")
REGISTRY_CLIENT_ID=$(getClientIdentifierByClientId "uk-ets-registry-api")
ROLE_REPRESENTATION="[{\"id\": $ROLE_ID, \"name\": \"site-publisher\"}]"

echo "Adding role to all admin users..."

USER_IDS=$(getUsersInRoleName "$REGISTRY_CLIENT_ID" "senior-registry-administrator")
addRoleToUsers "$ROLE_REPRESENTATION" "$USER_IDS" "$NEW_PUBLICATION_CLIENT_IDENTIFIER"

USER_IDS=$(getUsersInRoleName "$REGISTRY_CLIENT_ID" "junior-registry-administrator")
addRoleToUsers "$ROLE_REPRESENTATION" "$USER_IDS" "$NEW_PUBLICATION_CLIENT_IDENTIFIER"

echo "Roles added to admin users"

# adding new role to all existing authority users
echo "Adding role to authority users..."

USER_IDS=$(getUsersInRoleName "$REGISTRY_CLIENT_ID" "authority-user")
addRoleToUsers "$ROLE_REPRESENTATION" "$USER_IDS" "$NEW_PUBLICATION_CLIENT_IDENTIFIER"

echo "Roles added to authority users"
