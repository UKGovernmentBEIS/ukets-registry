# This migration adds the reports-user role to authorized representatives

REPORTS_CLIENT_IDENTIFIER=$(getClientIdentifierByClientId "uk-ets-reports-api")
ROLE_ID=$(getClientRoleByName "$REPORTS_CLIENT_IDENTIFIER" "reports-user")
REGISTRY_CLIENT_ID=$(getClientIdentifierByClientId "uk-ets-registry-api")
ROLE_REPRESENTATION="[{\"id\": $ROLE_ID, \"name\": \"reports-user\"}]"

echo "Adding reporting role to authorized representative users..."

USER_IDS=$(getUsersInRoleName "$REGISTRY_CLIENT_ID" "authorized-representative")
addRoleToUsers "$ROLE_REPRESENTATION" "$USER_IDS" "$REPORTS_CLIENT_IDENTIFIER"

echo "Role added to authorized representative users"
