# Disables 'email' event listener in UK ETS realm Event Config

echo "23.1 Started process to disable 'email' event listener in UK ETS realm Event Config"

# Update event listeners, remove email.
ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/events/config" -d @"$BASE_FILE_PATH"/migrations/migration_0023-remove-email-event-listener/uk-ets-remove-email-event-listener.json --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Events updated successfully"
else
  echo "Events were not updated successfully, exiting..."
  exit 2
fi