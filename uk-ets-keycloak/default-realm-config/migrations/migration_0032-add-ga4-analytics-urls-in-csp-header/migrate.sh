# This migration updates the Keycloak CSP headers to include the google analytics ga4 URL.
# See also the migration_0020-add-analytics-urls-in-csp-header folder and comments therein. 

echo "Update CSP for GA4 configuration..."
ACCESS_TOKEN=$(getAccessToken)

HTTP_CODE=$(
  curl -s -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}" -d @"$BASE_FILE_PATH"/migrations/migration_0032-add-ga4-analytics-urls-in-csp-header/uk-ets-realm-csp-header.json --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Updated CSP for GA4 configuration successfully"
else
  echo "CSP for GA4 was not configured successfully, exiting..."
  exit 2
fi
