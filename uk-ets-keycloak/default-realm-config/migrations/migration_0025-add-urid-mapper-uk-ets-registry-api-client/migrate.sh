# This migration adds the urid attribute mapper to uk-ets-registry-api client

echo "Adding urid attribute mapper to uk-ets-registry-api client..."
ACCESS_TOKEN=$(getAccessToken)
REGISTRY_CLIENT_ID=$(getClientIdentifierByClientId "uk-ets-registry-api")
HTTP_CODE=$(
  curl -s -XPOST -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/clients/$REGISTRY_CLIENT_ID/protocol-mappers/models" -d @"$BASE_FILE_PATH"/migrations/migration_0025-add-urid-mapper-uk-ets-registry-api-client/urid-attribute-mapper.json --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 201 ]]; then
  echo "Urid attribute mapper for uk-ets-registry-api client was configured successfully"
else
  echo "Urid attribute mapper for uk-ets-registry-api client was not configured successfully, exiting..."
  exit 2
fi

