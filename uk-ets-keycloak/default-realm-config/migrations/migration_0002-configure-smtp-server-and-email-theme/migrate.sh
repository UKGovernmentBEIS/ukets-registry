# Configures SMTP Server and also the theme for the emails sent by Keycloak.

TEMP_FILE_NAME="$BASE_FILE_PATH"/migrations/migration_0002-configure-smtp-server-and-email-theme/uk-ets-updated-realm-config_updated.json
echo "Updating the realm with smtp server and email theme config..."
# replace environment specific variables for smtp server
# we also need to escape all special characters (sed 's/[^a-zA-Z 0-9]/\\&/g')
# create a temp file so that the original file is not modified
if
  ! sed -e "s/KEYCLOAK_MAIL_TLS/$(echo $KEYCLOAK_MAIL_TLS | sed 's/[^a-zA-Z 0-9]/\\&/g')/g" \
  -e "s/KEYCLOAK_MAIL_AUTH/$(echo $KEYCLOAK_MAIL_AUTH | sed 's/[^a-zA-Z 0-9]/\\&/g')/g" \
  -e "s/KEYCLOAK_MAIL_PORT/$(echo $KEYCLOAK_MAIL_PORT | sed 's/[^a-zA-Z 0-9]/\\&/g')/g" \
  -e "s/KEYCLOAK_MAIL_HOST/$(echo $KEYCLOAK_MAIL_HOST | sed 's/[^a-zA-Z 0-9]/\\&/g')/g" \
  -e "s/KEYCLOAK_MAIL_FROM/$(echo $KEYCLOAK_MAIL_FROM | sed 's/[^a-zA-Z 0-9]/\\&/g')/g" \
  -e "s/KEYCLOAK_MAIL_SSL/$(echo $KEYCLOAK_MAIL_SSL | sed 's/[^a-zA-Z 0-9]/\\&/g')/g" \
  -e "s/KEYCLOAK_MAIL_USER/$(echo $KEYCLOAK_MAIL_USER | sed 's/[^a-zA-Z 0-9]/\\&/g')/g" \
  -e "s/KEYCLOAK_MAIL_PASSWORD/$(echo $KEYCLOAK_MAIL_PASSWORD | sed 's/[^a-zA-Z 0-9]/\\&/g')/g" \
  "$BASE_FILE_PATH"/migrations/migration_0002-configure-smtp-server-and-email-theme/uk-ets-updated-realm-config.json >${TEMP_FILE_NAME}
then
  echo "Failed to update uk-ets-updated-realm-config.json file with environment specific values"
  exit 2
fi

escapeJsonSpecialChars "$TEMP_FILE_NAME"

ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}" -d @${TEMP_FILE_NAME} --write-out "%{http_code}\n"
)
# remove the temporary file after the request
rm $TEMP_FILE_NAME

if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Realm updated successfully"
else
  echo "Realm was not updated successfully, exiting..."
  exit 2
fi
