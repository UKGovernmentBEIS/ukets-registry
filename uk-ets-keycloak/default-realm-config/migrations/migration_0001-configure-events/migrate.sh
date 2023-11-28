# Enables events, event listeners needed for emails and event types.

echo "Configuring Events..."
ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/events/config" -d @"$BASE_FILE_PATH"/migrations/migration_0001-configure-events/uk-ets-realm-events-config.json --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Events configured successfully"
else
  echo "Events were not configured successfully, exiting..."
  exit 2
fi
