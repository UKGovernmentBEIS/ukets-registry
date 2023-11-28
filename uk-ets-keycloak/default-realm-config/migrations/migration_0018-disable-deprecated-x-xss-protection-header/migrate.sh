
echo "Setting X-XSS-Protection header set to 0..."

ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}" -d @"$BASE_FILE_PATH"/migrations/migration_0018-disable-deprecated-x-xss-protection-header/uk-ets-realm-disable-xss-protection.json --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 204 ]]; then
  echo "X-XSS-Protection header set to 0"
else
  echo "X-XSS-Protection header was not set to 0, exiting..."
  exit 2
fi
