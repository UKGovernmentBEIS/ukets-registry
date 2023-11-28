#!/bin/bash

mvn clean install
# Disabling docker creation for now see UKETS-3157
#ENV_VERSION=`mvn help:evaluate -Dexpression=project.version -q -DforceStdout`
#sed -i "s/env_version/$ENV_VERSION/g" Dockerfile
#docker image build -t uk-ets-signing:latest -f Dockerfile .
#sed -i "s/$ENV_VERSION/env_version/g" Dockerfile
