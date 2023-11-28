echo "Configure authentication action..."
ACCESS_TOKEN=$(getAccessToken)

HTTP_CODE=$(
  curl -s -XPOST -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/authentication/flows/UK%20ETS%20browser%20forms/executions/execution" -d @"$BASE_FILE_PATH"/migrations/migration_0019-add-user-session-limiter/uk-ets-realm-add-execution.json --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 201 ]]; then
  echo "add User Session Limiter execution successfully"
else
  echo "add User Session Limiter execution was not configured successfully, exiting..."
  exit 2
fi