# Update event listeners, adding event-producer.

echo "Updating Event listeners..."
ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/events/config" -d @"$BASE_FILE_PATH"/migrations/migration_0004-enable-event-producer-listener/uk-ets-updated-event-listeners.json --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Event listeners updated successfully"
else
  echo "Event listeners were not updated successfully, exiting..."
  exit 2
fi
