# This migration adds the reports-user role to the service account

REPORTS_CLIENT_IDENTIFIER=$(getClientIdentifierByClientId "uk-ets-reports-api")
ROLE_ID=$(getClientRoleByName "$REPORTS_CLIENT_IDENTIFIER" "reports-user")
ROLE_REPRESENTATION="[{\"id\": $ROLE_ID, \"name\": \"reports-user\"}]"
REGISTRY_CLIENT_ID=$(getClientIdentifierByClientId "uk-ets-registry-api")

echo "Retrieving Service Account user..."
SERVICE_ACCOUNT_USER_ID=$(getSystemAccountUserIdByClientId "$REGISTRY_CLIENT_ID")

echo "Adding reporting role to service account..."

addRoleToUsers "$ROLE_REPRESENTATION" "$SERVICE_ACCOUNT_USER_ID" "$REPORTS_CLIENT_IDENTIFIER"

echo "Role added to service account"
