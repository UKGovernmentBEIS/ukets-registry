# This migration adds the site-publisher role to the service account

PUBLICATION_CLIENT_IDENTIFIER=$(getClientIdentifierByClientId "uk-ets-publication-api")
ROLE_ID=$(getClientRoleByName "$PUBLICATION_CLIENT_IDENTIFIER" "site-publisher")
ROLE_REPRESENTATION="[{\"id\": $ROLE_ID, \"name\": \"site-publisher\"}]"
REGISTRY_CLIENT_ID=$(getClientIdentifierByClientId "uk-ets-registry-api")

echo "Retrieving Service Account user..."
SERVICE_ACCOUNT_USER_ID=$(getSystemAccountUserIdByClientId "$REGISTRY_CLIENT_ID")

echo "Adding publication role to service account..."

addRoleToUsers "$ROLE_REPRESENTATION" "$SERVICE_ACCOUNT_USER_ID" "$PUBLICATION_CLIENT_IDENTIFIER"

echo "Role added to service account"