# Removes access to account management pages hosted on Keycloak, for all newly registered users. These web pages,
# allow a user to change some of his details outside the registry application, including his password and otp device.

echo "16.1 Started process to disable access to Keycloak account management pages"

ACCESS_TOKEN=$(getAccessToken)
IDENTIFIER=$(
  curl -X GET "${BASE_REALM_URL}/clients" \
-H "Accept: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" | jq '.[] | select(.clientId == "account") | .id'
)
echo "$IDENTIFIER"
echo "16.2 Retrieved account client identifier in UK ETS Realm"
IDENTIFIER=$(sed -e 's/^"//' -e 's/"$//' <<<"$IDENTIFIER")
echo "$IDENTIFIER"

HTTP_CODE=$(
curl -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' "${BASE_REALM_URL}/clients/${IDENTIFIER}" -d '{"defaultRoles": ["view-profile"]}' \
-H "Accept: application/json" \
-H "Authorization: Bearer $ACCESS_TOKEN" --write-out "%{http_code}\n")
echo "$HTTP_CODE"
if [[ $HTTP_CODE -eq 204 ]]; then
  echo "16.3 Access to Keycloak account management pages is disabled for all new users"
else
  echo "16.4 Error when disabling access to Keycloak account management pages. The process will exit"
  exit 2
fi
