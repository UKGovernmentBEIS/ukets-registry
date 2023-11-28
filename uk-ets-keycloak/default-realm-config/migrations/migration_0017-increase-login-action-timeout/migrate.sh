# Updates Login action timeout to 10 minutes

echo "Updating Login Action Timeout..."

ACCESS_TOKEN=$(getAccessToken)
HTTP_CODE=$(
  curl -s -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}" -d "{\"accessCodeLifespanUserAction\": 600}" --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Login Action Timeout updated successfully"
else
  echo "Login Action Timeout was not added successfully, exiting..."
  exit 2
fi
