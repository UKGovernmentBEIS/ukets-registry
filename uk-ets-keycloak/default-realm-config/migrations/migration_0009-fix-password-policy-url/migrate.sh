# Sets default password policy for UK ETS to uk-ets-password-policy
TEMP_FILE_NAME="$BASE_FILE_PATH"/migrations/migration_0009-fix-password-policy-url/uk-ets-realm-updated-policy-temp.json

if [ "${PASSWORD_VALIDATOR:+set}" != set ]; then
  echo "Environmental variable PASSWORD_VALIDATOR is required. Migration aborted."
  exit 2
fi

if
  ! sed -e "s/PASSWORD_VALIDATOR/$(echo $PASSWORD_VALIDATOR | sed 's/[^a-zA-Z 0-9]/\\&/g')/g" \
  "$BASE_FILE_PATH"/migrations/migration_0009-fix-password-policy-url/uk-ets-realm-updated-policy.json >${TEMP_FILE_NAME}
then
  echo "Failed to update uk-ets-updated-realm-config.json file with environment specific values"
  exit 2
fi

echo "Updating password policy..."
ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}" -d @${TEMP_FILE_NAME} --write-out "%{http_code}\n"
)

# remove the temporary file after the request
rm $TEMP_FILE_NAME

if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Fixed password policy to 'uk-ets-password-policy'"
else
  echo "Failed to fix password policy to 'uk-ets-password-policy(url)', exiting..."
  exit 2
fi
