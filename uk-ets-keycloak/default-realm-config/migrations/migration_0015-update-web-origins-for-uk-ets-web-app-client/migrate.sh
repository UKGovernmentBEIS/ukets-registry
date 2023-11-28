# Updates Web Origins for uk-ets-web-app client to '+'. This tells keycloak to allow origins same as the valid Redirect URIs

# Retrieve unique identifier of uk-ets-web-app client
echo "Retrieving uk-ets-web-app details from UK ETS realm"
ACCESS_TOKEN=$(getAccessToken)
IDENTIFIER=$(
  curl -s -X GET "${BASE_REALM_URL}/clients" \
-H "Accept: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" | jq '.[] | select(.clientId == "uk-ets-web-app") | .id'
)

IDENTIFIER=$(sed -e 's/^"//' -e 's/"$//' <<<"$IDENTIFIER")

echo "Updating Web Origins..."
HTTP_CODE=$(
curl -s -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' "${BASE_REALM_URL}/clients/${IDENTIFIER}" -d '{"webOrigins": ["+"]}' \
-H "Accept: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" --write-out "%{http_code}\n")

if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Web Origins updated successfully"
else
  echo "Error when updating Web Origins. The process will exit"
  exit 2
fi
