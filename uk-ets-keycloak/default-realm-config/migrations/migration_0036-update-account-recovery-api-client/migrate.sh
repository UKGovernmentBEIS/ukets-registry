# This migration performs the following steps:
# 1. Deletes and re-create the uk-netz-api-account-recovery Keycloak Client.
# 2. Creates realm-management client roles for the new service account user (this user was created automatically by step 1).
# 3. Creates the client role account-recovery-user for the new uk-netz-api-account-recovery client.

CLIENT_FILE_PATH=migrations/migration_0036-update-account-recovery-api-client/uk-ets-realm-account-recovery-api.json
INITIAL_CLIENT_FILE="$BASE_FILE_PATH/$CLIENT_FILE_PATH"
UPDATED_CLIENT_FILE=$BASE_FILE_PATH/migrations/temp_updated_client.json

# Use default BASE_URL if not found in environment
if [ "${ACCOUNT_RECOVERY_API_BASE_URL:+set}" != set ]; then
  ACCOUNT_RECOVERY_API_BASE_URL=http://localhost:8888
fi

# Use default KEYCLOAK_RECOVERY_ACCOUNT_CLIENT_SECRET if not found in environment
if [ "${KEYCLOAK_RECOVERY_ACCOUNT_CLIENT_SECRET:+set}" != set ]; then
  KEYCLOAK_RECOVERY_ACCOUNT_CLIENT_SECRET="LtuoPk55CuNF0zn83bqAHGFZeD01RbKB"
fi

if
  ! sed -e "s/ACCOUNT_RECOVERY_API_BASE_URL/$(echo $ACCOUNT_RECOVERY_API_BASE_URL | sed 's/[^a-zA-Z 0-9]/\\&/g')/g" \
  -e "s/KEYCLOAK_RECOVERY_ACCOUNT_CLIENT_SECRET/$(echo $KEYCLOAK_RECOVERY_ACCOUNT_CLIENT_SECRET | sed 's/[^a-zA-Z 0-9]/\\&/g')/g" \
  "${INITIAL_CLIENT_FILE}" >"${UPDATED_CLIENT_FILE}"
then
  echo "Failed to update ${INITIAL_CLIENT_FILE} file with environment specific values"
  exit 2
fi
escapeJsonSpecialChars "$UPDATED_CLIENT_FILE"

echo "Retrieving uk-netz-api-account-recovery id from UK ETS realm"

CLIENT_IDENTIFIER=$(getClientIdentifierByClientId "uk-netz-api-account-recovery")

if [ -n "$CLIENT_IDENTIFIER" ]; then
  # client exists, first delete it. NOTE: service account user and roles (i.e. account-recovery-user) are also deleted
  echo "Deleting uk-netz-api-account-recovery client..."
  ACCESS_TOKEN=$(getAccessToken)
  HTTP_CODE=$(
    curl -s -XDELETE -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
    "${BASE_REALM_URL}/clients/${CLIENT_IDENTIFIER}" \
    --write-out "%{http_code}\n"
  )

  if [[ $HTTP_CODE -eq 204 ]]; then
    echo "Account Recovery API Client deleted"
  else
    echo "Account Recovery API Client was not deleted exiting..."
    exit 2
  fi
fi

## create client
echo "Creating Account Recovery API Client..."
ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPOST -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/clients" -d @"$UPDATED_CLIENT_FILE" --write-out "%{http_code}\n"
)

rm "$UPDATED_CLIENT_FILE"

if [[ $HTTP_CODE -eq 201 ]]; then
  echo "Account Recovery API Client created"
else
  echo "Account Recovery API Client was not created exiting..."
  exit 2
fi

# create realm-management client roles for service account user
echo "Retrieving Service Account user..."
NEW_ACCOUNT_RECOVERY_CLIENT_IDENTIFIER=$(getClientIdentifierByClientId "uk-netz-api-account-recovery")
SERVICE_ACCOUNT_USER_IDENTIFIER=$(getSystemAccountUserIdByClientId "$NEW_ACCOUNT_RECOVERY_CLIENT_IDENTIFIER")

echo "Setting Service Account client roles for uk-netz-api-account-recovery client..."
REALM_MANAGEMENT_CLIENT_IDENTIFIER=$(getClientIdentifierByClientId "realm-management")

HTTP_CODE=$(
  setClientRolesByUserIdAndClientId "$SERVICE_ACCOUNT_USER_IDENTIFIER" "$REALM_MANAGEMENT_CLIENT_IDENTIFIER" \
  "migration_0036-update-account-recovery-api-client/uk-ets-realm-account-recovery-api-realm-management-roles.json"
)

if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Realm-management client roles added to service account user"
else
  echo "Realm-management client roles were not added to service account user exiting..."
  exit 2
fi

# create new client role 'account-recovery-user'
echo "Adding new role for client uk-netz-api-account-recovery..."

HTTP_CODE=$(addRoleToClient "$NEW_ACCOUNT_RECOVERY_CLIENT_IDENTIFIER" "account-recovery-user")

if [[ $HTTP_CODE -eq 201 ]]; then
  echo "New role for client added"
else
  echo "Role was not added to client exiting..."
  exit 2
fi