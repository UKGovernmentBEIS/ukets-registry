#!/bin/bash

quick=$1

if [ "$quick" == 'quickly' ];
then
  mvn -P buildQuickly clean install
else
  if [ "$quick" == 'noWeb' ];
  then
    mvn -pl "!uk.gov.ets:uk-ets-registry-web" -P buildQuickly clean install
  else
    mvn clean install
  fi
fi
