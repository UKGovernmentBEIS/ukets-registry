#!/bin/bash

mvn clean install -Plocal
ENV_VERSION=`mvn help:evaluate -Dexpression=project.version -q -DforceStdout`
sed -i "s/env_version/$ENV_VERSION/g" Dockerfile
docker image build -t uk-ets-app-ui-logs:latest -f Dockerfile .
sed -i "s/$ENV_VERSION/env_version/g" Dockerfile