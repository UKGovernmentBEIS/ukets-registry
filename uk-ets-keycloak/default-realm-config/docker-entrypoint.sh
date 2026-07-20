#!/bin/bash

# usage: file_env VAR [DEFAULT]
#    ie: file_env 'XYZ_KC_DB_PASSWORD' 'example'
# (will allow for "$XYZ_KC_DB_PASSWORD_FILE" to fill in the value of
#  "$XYZ_KC_DB_PASSWORD" from a file, especially for Docker's secrets feature)
file_env() {
    local var="$1"
    local fileVar="${var}_FILE"
    local def="${2:-}"
    if [[ ${!var:-} && ${!fileVar:-} ]]; then
        echo >&2 "error: both $var and $fileVar are set (but are exclusive)"
        exit 1
    fi
    local val="$def"
    if [[ ${!var:-} ]]; then
        val="${!var}"
    elif [[ ${!fileVar:-} ]]; then
        val="$(< "${!fileVar}")"
    fi

    if [[ -n $val ]]; then
        export "$var"="$val"
    fi

    unset "$fileVar"
}

SYS_PROPS=""

##################
# Add admin user #
##################

file_env 'KEYCLOAK_ADMIN'
file_env 'KEYCLOAK_ADMIN_PASSWORD'

#################
# Configuration #
#################

# Kafka Authentication
if [ "${KAFKA_AUTHENTICATION_ENABLED:+set}" != set ]; then
  echo "Environmental variable KAFKA_AUTHENTICATION_ENABLED is not set."

elif [ "${KAFKA_AUTHENTICATION_ENABLED}" == "false" ]; then
  echo "Environmental variable KAFKA_AUTHENTICATION_ENABLED is set  to false."
 
elif [ "${KAFKA_AUTHENTICATION_ENABLED}" == "true" ]; then
  echo "Environmental variable KAFKA_AUTHENTICATION_ENABLED is set  to true."
fi

##################
# Start Keycloak #
##################

/opt/keycloak/bin/kc.sh $STARTUP_COMMAND &
sleep 90
/opt/keycloak/tools/default-setup.sh
sleep infinity