# Sets default password policy for UK ETS to uk-ets-password-policy

echo "Updating password policy..."
ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}" -d @"$BASE_FILE_PATH"/migrations/migration_0005-enable-uk-ets-password-policy/uk-ets-realm-updated-policy.json --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Updated password policy to 'uk-ets-password-policy'"
else
  echo "Failed to set to password policy to 'uk-ets-password-policy', exiting..."
  exit 2
fi
