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

# Configure Kafka Authentication
if [ "${KAFKA_AUTHENTICATION_ENABLED:+set}" != set ]; then
  echo "Environmental variable KAFKA_AUTHENTICATION_ENABLED is not set, skipping configuration"

elif [ "${KAFKA_AUTHENTICATION_ENABLED}" == "false" ]; then
  echo "Environmental variable KAFKA_AUTHENTICATION_ENABLED is set  to false, skipping configuration"

elif [ "${KAFKA_SASL_JAAS_CONFIG:+set}" != set ]; then
  echo "Environmental variable KAFKA_SASL_JAAS_CONFIG is not set, \
  generating it using KAFKA_CLIENT_USERNAME and KAFKA_CLIENT_PASSWORD..."
  if [ "${KAFKA_CLIENT_USERNAME:+set}" != set ]; then
    echo "ERROR: Environmental variable KAFKA_CLIENT_USERNAME is required. Startup aborted."
    exit 2
  fi
  if [ "${KAFKA_CLIENT_PASSWORD:+set}" != set ]; then
    echo "ERROR: Environmental variable KAFKA_CLIENT_PASSWORD is required. Startup aborted."
    exit 2
  fi

  export KAFKA_SASL_JAAS_CONFIG="org.apache.kafka.common.security.scram.ScramLoginModule required \
  username=\"${KAFKA_CLIENT_USERNAME}\" password=\"${KAFKA_CLIENT_PASSWORD}\";"

  echo "Environmental variable KAFKA_SASL_JAAS_CONFIG generated"
fi

##################
# Start Keycloak #
##################

/opt/keycloak/bin/kc.sh $STARTUP_COMMAND &
sleep 45
/opt/keycloak/tools/default-setup.sh
sleep infinity