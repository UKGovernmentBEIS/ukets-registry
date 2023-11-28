#!/bin/bash

mvn clean install -P instrument
mvn sonar:sonar

cd uk-ets-registry-web
./_gather-metrics.sh
cd ..