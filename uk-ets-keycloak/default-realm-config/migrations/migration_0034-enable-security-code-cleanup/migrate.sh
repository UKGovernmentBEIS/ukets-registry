echo "Enable cleanup-security-code action..."
ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XGET -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/authentication/required-actions/cleanup-security-code" --output /dev/null --write-out "%{http_code}\n"
)

if [[ $HTTP_CODE -eq 200 ]]; then
  echo "cleanup-security-code action already exists, exiting..."
else
  echo "Adding cleanup-security-code action"
  ACCESS_TOKEN=$(getAccessToken)
  HTTP_CODE=$(
    curl -s -XPOST -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
    "${BASE_REALM_URL}/authentication/register-required-action" -d @"$BASE_FILE_PATH"/migrations/migration_0034-enable-security-code-cleanup/uk-ets-realm-cleanup-security-code.json --write-out "%{http_code}\n"
  )

  if [[ $HTTP_CODE -eq 204 ]]; then
    echo "cleanup-security-code action configured successfully"
  else
    echo "cleanup-security-code action was not configured successfully, exiting..."
    exit 2
  fi
fi