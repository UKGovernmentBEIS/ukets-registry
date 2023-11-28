#!/bin/bash

mvn clean install
# shellcheck disable=SC2181
if [[ "$?" -ne 0 ]]; then
  echo 'Maven build failed'
  exit 2
fi
ENV_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
sed -i "s/env_version/$ENV_VERSION/g" Dockerfile
dos2unix default-realm-config/default-setup.sh
dos2unix default-realm-config/docker-entrypoint.sh
# run dos2unix recursively in all migration files:
find default-realm-config/migrations/ -type f -print0 | xargs -0 dos2unix
docker image build -t keycloak-internal:latest -f Dockerfile .
sed -i "s/$ENV_VERSION/env_version/g" Dockerfile
# revert dos2unix operations to bypass issue with LF showing as changed files in windows.
unix2dos default-realm-config/default-setup.sh
unix2dos default-realm-config/docker-entrypoint.sh
find default-realm-config/migrations/ -type f -print0 | xargs -0 unix2dos
unix2dos Dockerfile
