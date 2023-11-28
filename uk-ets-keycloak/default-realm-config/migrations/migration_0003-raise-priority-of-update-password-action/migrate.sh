# Raises priority of UPDATE_PASSWORD actions, so that it has a higher priority than CHANGE_OTP action.
# This way it is presented first on the user when both actions are required.

echo "Raise priority of UPDATE_PASSWORD action..."
ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPOST -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}/authentication/required-actions/UPDATE_PASSWORD/raise-priority" --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 204 ]]; then
  echo "UPDATE_PASSWORD action configured successfully"
else
  echo "UPDATE_PASSWORD action was not configured successfully, exiting..."
  exit 2
fi
