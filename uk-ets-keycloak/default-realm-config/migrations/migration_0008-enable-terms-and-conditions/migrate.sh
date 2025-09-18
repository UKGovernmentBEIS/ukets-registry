echo "Enable terms_and_conditions action..."
ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/authentication/required-actions/TERMS_AND_CONDITIONS" -d @"$BASE_FILE_PATH"/migrations/migration_0008-enable-terms-and-conditions/uk-ets-realm-terms-and-conditions.json --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 204 ]]; then
  echo "terms_and_conditions action configured successfully"
else
  echo "terms_and_conditions action was not configured successfully, exiting..."
  exit 2
fi

