# This migration performs the following steps:
# 1. Deletes and re-create the uk-ets-reports-api Keycloak Client.
# 2. Creates realm-management client roles for the new service account user (this user was created automatically by step 1).
# 3. Creates the client role reports-user for the new uk-ets-reports-api client.
# 4. Adds the new role to all existing senior and junior administrators.

CLIENT_FILE_PATH=migrations/migration_0021-add-reports-api-client/uk-ets-realm-reports-api-client.json
INITIAL_CLIENT_FILE="$BASE_FILE_PATH/$CLIENT_FILE_PATH"
UPDATED_CLIENT_FILE=$BASE_FILE_PATH/migrations/temp_updated_client.json

# Use default BASE_URL if not found in environment
if [ "${REPORTS_API_BASE_URL:+set}" != set ]; then
  REPORTS_API_BASE_URL=http://localhost:8097
fi
if
  ! sed -e "s/REPORTS_API_BASE_URL/$(echo $REPORTS_API_BASE_URL | sed 's/[^a-zA-Z 0-9]/\\&/g')/g" \
  "${INITIAL_CLIENT_FILE}" >"${UPDATED_CLIENT_FILE}"
then
  echo "Failed to update ${INITIAL_CLIENT_FILE} file with environment specific values"
  exit 2
fi
escapeJsonSpecialChars "$UPDATED_CLIENT_FILE"

echo "Retrieving uk-ets-reports-api id from UK ETS realm"

CLIENT_IDENTIFIER=$(getClientIdentifierByClientId "uk-ets-reports-api")

if [ -n "$CLIENT_IDENTIFIER" ]; then
  # client exists, first delete it. NOTE: service account user and roles (i.e. reports-user) are also deleted
  echo "Deleting uk-ets-reports-api client..."
  ACCESS_TOKEN=$(getAccessToken)
  HTTP_CODE=$(
    curl -s -XDELETE -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
    "${BASE_REALM_URL}/clients/${CLIENT_IDENTIFIER}" \
    --write-out "%{http_code}\n"
  )

  if [[ $HTTP_CODE -eq 204 ]]; then
    echo "Reports API Client deleted"
  else
    echo "Reports API Client was not deleted exiting..."
    exit 2
  fi
fi

## create client
echo "Creating Reports API Client..."
ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPOST -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/clients" -d @"$UPDATED_CLIENT_FILE" --write-out "%{http_code}\n"
)

rm "$UPDATED_CLIENT_FILE"

if [[ $HTTP_CODE -eq 201 ]]; then
  echo "Reports API Client created"
else
  echo "Reports API Client was not created exiting..."
  exit 2
fi

# create realm-management client roles for service account user
echo "Retrieving Service Account user..."
NEW_REPORTS_CLIENT_IDENTIFIER=$(getClientIdentifierByClientId "uk-ets-reports-api")
SERVICE_ACCOUNT_USER_IDENTIFIER=$(getSystemAccountUserIdByClientId "$NEW_REPORTS_CLIENT_IDENTIFIER")

echo "Setting Service Account client roles for uk-ets-reports-api client..."
REALM_MANAGEMENT_CLIENT_IDENTIFIER=$(getClientIdentifierByClientId "realm-management")

HTTP_CODE=$(
  setClientRolesByUserIdAndClientId "$SERVICE_ACCOUNT_USER_IDENTIFIER" "$REALM_MANAGEMENT_CLIENT_IDENTIFIER" \
  "migration_0021-add-reports-api-client/uk-ets-realm-reports-realm-management-roles.json"
)

if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Realm-management client roles added to service account user"
else
  echo "Realm-management client roles were not added to service account user exiting..."
  exit 2
fi

# create new client role 'reports-user'
echo "Adding new role for client uk-ets-reporting-api..."

HTTP_CODE=$(addRoleToClient "$NEW_REPORTS_CLIENT_IDENTIFIER" "reports-user")

if [[ $HTTP_CODE -eq 201 ]]; then
  echo "New role for client added"
else
  echo "Role was not added to client exiting..."
  exit 2
fi

# adding new role to all existing administrators
ROLE_ID=$(getClientRoleByName "$NEW_REPORTS_CLIENT_IDENTIFIER" "reports-user")
REGISTRY_CLIENT_ID=$(getClientIdentifierByClientId "uk-ets-registry-api")
ROLE_REPRESENTATION="[{\"id\": $ROLE_ID, \"name\": \"reports-user\"}]"

echo "Adding role to all admin users..."

USER_IDS=$(getUsersInRoleName "$REGISTRY_CLIENT_ID" "senior-registry-administrator")
addRoleToUsers "$ROLE_REPRESENTATION" "$USER_IDS" "$NEW_REPORTS_CLIENT_IDENTIFIER"

USER_IDS=$(getUsersInRoleName "$REGISTRY_CLIENT_ID" "junior-registry-administrator")
addRoleToUsers "$ROLE_REPRESENTATION" "$USER_IDS" "$NEW_REPORTS_CLIENT_IDENTIFIER"

echo "Roles added to admin users"
