echo "Configure authentication action..."
ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPOST --output /dev/null -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/authentication/flows/browser/copy" -d @"$BASE_FILE_PATH"/migrations/migration_0010-configure-authentication/uk-ets-realm-copy-browser-flow.json --write-out "%{http_code}\n"
)

if [[ $HTTP_CODE -eq 409 ]]; then
  echo "Flow already exists -- deleting and recreating"
  deleteExistingFlowAndCopy
elif [[ $HTTP_CODE -eq 201 ]]; then
  echo "copy of existing flow action successfully"
else
  echo "copy of existing flow action was not configured successfully, exiting..."
  exit 2
fi

USERNAME_PASSWORD_EXECUTION_ID=$(
  curl -s -H 'Accept: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/authentication/flows/UK%20ETS%20browser/executions" | jq -r '.[] | select(.displayName=="Username Password Form") | .id'
)

HTTP_CODE=$(
  curl -s -XDELETE -H 'Accept: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/authentication/executions/$USERNAME_PASSWORD_EXECUTION_ID" --write-out "%{http_code}\n"
)

if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Username Password Form execution deleted successfully"
else
  echo "Username Password Form execution was not deleted successfully, exiting..."
  exit 2
fi

HTTP_CODE=$(
  curl -s -XPOST -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/authentication/flows/UK%20ETS%20browser%20forms/executions/execution" -d @"$BASE_FILE_PATH"/migrations/migration_0010-configure-authentication/uk-ets-realm-add-execution.json --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 201 ]]; then
  echo "add UK ETS Username Passwd Form execution successfully"
else
  echo "add UK ETS Username Passwd Form execution was not configured successfully, exiting..."
  exit 2
fi

UK_ETS_USERNAME_PASSWORD_EXECUTION_ID=$(
  curl -s -H 'Accept: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/authentication/flows/UK%20ETS%20browser/executions" | jq -r '.[] | select(.displayName=="UK ETS Username Password Form") | .id'
)

HTTP_CODE=$(
  curl -s -XPOST -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/authentication/executions/$UK_ETS_USERNAME_PASSWORD_EXECUTION_ID/raise-priority" --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 204 ]]; then
  echo "raise priority UK ETS Username Passwd Form execution successfully"
else
  echo "raise priority UK ETS Username Passwd Form execution was not configured successfully, exiting..."
  exit 2
fi

HTTP_CODE=$(
  curl -s -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}" -d @"$BASE_FILE_PATH"/migrations/migration_0010-configure-authentication/uk-ets-realm-update-bindings.json --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 204 ]]; then
  echo "update uk-ets realm bindings execution successfully"
else
  echo "update uk-ets realm bindings execution was not configured successfully, exiting..."
  exit 2
fi
