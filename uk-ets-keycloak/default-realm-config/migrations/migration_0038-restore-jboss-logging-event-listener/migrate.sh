# Re-enables 'jboss-logging' event listener in UK ETS realm Event Config

echo "38.1 Started process to re-enable 'jboss-logging' event listener in UK ETS realm Event Config"

# Update event listeners, add jboss-logging.
ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/events/config" -d @"$BASE_FILE_PATH"/migrations/migration_0038-restore-jboss-logging-event-listener/uk-ets-restore-jboss-logging-event-listener.json --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 204 ]]; then
  echo "'jboss-logging' event listener updated successfully"
else
  echo "'jboss-logging' event listener was not updated successfully, exiting..."
  exit 2
fi