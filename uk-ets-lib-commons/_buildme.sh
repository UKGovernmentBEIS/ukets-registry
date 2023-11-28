#!/bin/bash
# use a 'local' profile so that a standard log4j layout(and not json one) is selected when building for the local development env.
mvn clean install -Plocal
