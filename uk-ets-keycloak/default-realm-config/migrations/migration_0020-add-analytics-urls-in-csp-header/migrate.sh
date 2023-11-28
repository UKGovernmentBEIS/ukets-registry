# This migration updates the Keycloak CSP headers to include the google analytics URLs.
# The problem with this header is that as it was before, because the default CSP headers were used, Keycloak was using
# an internal property (allowAnyFrameAncestor) to remove the default CSP header concerning frame-ancestors which was frame-ancestors 'self'.
# This had as a result that when adding to the default CSP header the analytics CSP header, it reverted back to
# frame-ancestors 'self' and the cookies pages (which are framed) were blocked. To bypass this we removed
# the frame-ancestor directive completely. Furthermore, since we are adding a script directive, we must allow inline
# scripts (by default there was no script directive so inline scripts were allowed anyway)

echo "Update CSP configuration..."
ACCESS_TOKEN=$(getAccessToken)

HTTP_CODE=$(
  curl -s -XPUT -H 'Accept: application/json' -H 'Content-Type: application/json' -H "Authorization: Bearer $ACCESS_TOKEN" \
  "${BASE_REALM_URL}" -d @"$BASE_FILE_PATH"/migrations/migration_0020-add-analytics-urls-in-csp-header/uk-ets-realm-csp-header.json --write-out "%{http_code}\n"
)
if [[ $HTTP_CODE -eq 204 ]]; then
  echo "Updated CSP configuration successfully"
else
  echo "CSP was not configured successfully, exiting..."
  exit 2
fi
