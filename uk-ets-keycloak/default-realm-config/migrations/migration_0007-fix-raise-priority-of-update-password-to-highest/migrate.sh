# Fixes migrations 0003, raising priority until it is the highest i.e. 10.

echo "Raise priority of UPDATE_PASSWORD action..."

CURRENT_PRIORITY=1000

while [ "$CURRENT_PRIORITY" -gt 10 ]; do

  ACCESS_TOKEN=$(getAccessToken)
  HTTP_CODE=$(
    curl -s -XPOST -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
      "${BASE_REALM_URL}/authentication/required-actions/UPDATE_PASSWORD/raise-priority" --write-out "%{http_code}\n"
  )
  if [[ $HTTP_CODE -eq 204 ]]; then
    echo "UPDATE_PASSWORD action priority raised"
  else
    echo "UPDATE_PASSWORD action was not configured successfully, exiting..."
    exit 2
  fi

  ACCESS_TOKEN=$(getAccessToken)
  CURRENT_PRIORITY=$(
    curl -s -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
      "${BASE_REALM_URL}/authentication/required-actions/UPDATE_PASSWORD" | jq -r '.priority'
  )
done
