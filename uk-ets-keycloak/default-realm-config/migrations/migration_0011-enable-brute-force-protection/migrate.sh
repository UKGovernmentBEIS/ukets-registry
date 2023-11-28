
echo "Enable brute force protection..."

ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}" -d @"$BASE_FILE_PATH"/migrations/migration_0011-enable-brute-force-protection/uk-ets-realm-brute-force.json --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Brute force protection enabled successfully"
else
  echo "Brute force protection not enabled successfully, exiting..."
  exit 2
fi
