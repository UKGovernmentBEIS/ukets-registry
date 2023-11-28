
echo "Adding missing realm attributes..."

ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}" -d @"$BASE_FILE_PATH"/migrations/migration_0012-add-missing-realm-attributes/uk-ets-realm-missing-attributes.json --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Missing realm attributes added successfully"
else
  echo "Missing realm attributes were not added successfully, exiting..."
  exit 2
fi
