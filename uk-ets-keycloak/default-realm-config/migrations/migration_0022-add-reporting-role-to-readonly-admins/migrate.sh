# This migration adds the reports-user role to readonly admins

REPORTS_CLIENT_IDENTIFIER=$(getClientIdentifierByClientId "uk-ets-reports-api")
ROLE_ID=$(getClientRoleByName "$REPORTS_CLIENT_IDENTIFIER" "reports-user")
REGISTRY_CLIENT_ID=$(getClientIdentifierByClientId "uk-ets-registry-api")
ROLE_REPRESENTATION="[{\"id\": $ROLE_ID, \"name\": \"reports-user\"}]"

echo "Adding reporting role to  readonly admin users..."

USER_IDS=$(getUsersInRoleName "$REGISTRY_CLIENT_ID" "readonly-administrator")
addRoleToUsers "$ROLE_REPRESENTATION" "$USER_IDS" "$REPORTS_CLIENT_IDENTIFIER"

echo "Roles added to readonly admin users"
