#!/bin/bash

mvn clean install -DskipTests -P instrument
mvn sonar:sonar