# Updates Web Origins for uk-ets-registry-api client to '+'. This tells keycloak to allow origins same as the valid Redirect URIs
# Firstly, a GET request to retrieve the existing client configuration is executed
# and then a jq command is used to replace webOrigins with '+' on the retrieved json
# finally a PUT request with the resulting json is executed

# Retrieve unique identifier of uk-ets-registry-api client
echo "Retrieving uk-ets-web-app details from UK ETS realm"
ACCESS_TOKEN=$(getAccessToken)
IDENTIFIER=$(
  curl -s -X GET "${BASE_REALM_URL}/clients" \
-H "Accept: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" | jq '.[] | select(.clientId == "uk-ets-registry-api") | .id'
)

IDENTIFIER=$(sed -e 's/^"//' -e 's/"$//' <<<"$IDENTIFIER")

echo "Get existing client configuration and modify webOrigins..."
CONFIG_JSON=$(curl -s -XGET "${BASE_REALM_URL}/clients/${IDENTIFIER}" -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" | jq '.webOrigins = ["+"]')

echo "Updating Web Origins..."
HTTP_CODE=$(
curl -s -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" "${BASE_REALM_URL}/clients/${IDENTIFIER}" -d "$CONFIG_JSON" --write-out "%{http_code}\n")

if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Web Origins updated successfully"
else
  echo "Error when updating Web Origins. The process will exit"
  exit 2
fi
