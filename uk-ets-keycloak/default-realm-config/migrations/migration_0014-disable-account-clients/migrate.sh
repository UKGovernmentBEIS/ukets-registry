# Disables 'account-console' Keycloak client in UK ETS realm

echo "14.1 Started process to disable account-console Keycloak client in UK ETS realm"

# Retrieve unique identifier of account-console client
echo "14.2 About to retrieve ACCOUNT-CONSOLE details from UK ETS realm"
ACCESS_TOKEN=$(getAccessToken)
IDENTIFIER=$(
  curl -X GET "${BASE_REALM_URL}/clients" \
-H "Accept: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" | jq '.[] | select(.clientId == "account-console") | .id'
)
echo "14.3 Parsed ACCOUNT-CONSOLE unique identifier:"
echo "$IDENTIFIER"
echo "14.4 Stripping ACCOUNT-CONSOLE unique identifier from double quotes"
IDENTIFIER=$(sed -e 's/^"//' -e 's/"$//' <<<"$IDENTIFIER")
echo "$IDENTIFIER"

# Disable account-console client
echo "14.5 About to disable ACCOUNT-CONSOLE client in UK ETS realm"
HTTP_CODE=$(
curl -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' "${BASE_REALM_URL}/clients/${IDENTIFIER}" -d '{"enabled": false}' \
-H "Accept: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" --write-out "%{http_code}\n")
echo "$HTTP_CODE"
if [[ $HTTP_CODE -eq 204 ]]; then
  echo "14.6 ACCOUNT-CONSOLE client disabled successfully"
else
  echo "14.7 Error when disabling ACCOUNT-CONSOLE client. The process will exit"
  exit 2
fi
