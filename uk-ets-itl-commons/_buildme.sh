#!/bin/bash
mvn install:install-file -Dfile=./lib/activation-1.0.2.jar -DgroupId=javax.activation -DartifactId=activation -Dversion=1.0.2 -Dpackaging=jar -DgeneratePom=true
mvn clean install