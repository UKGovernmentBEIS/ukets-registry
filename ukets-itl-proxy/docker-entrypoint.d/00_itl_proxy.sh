#!/bin/bash
set -eu pipefail

ME=$(basename $0)
check_environment_vars()
{
    error=0
    vars=$@
    for v in "$@"; do
        if [[ -z $(printenv ${v}) ]]; then
            echo >&2 "$ME: FATAL: environment variable $v is not set"
            error=1
        fi
    done
    return $error
}

# Debugging options
DISABLE_INBOUND=${DISABLE_INBOUND:-FALSE}
DISABLE_OUTBOUND=${DISABLE_OUTBOUND:-FALSE}

# Defaults
INSTALL_ITL_SSL_CA=${INSTALL_ITL_SSL_CA:-FALSE}
INSTALL_EXTRA_CERTS=${INSTALL_EXTRA_CERTS:-FALSE}

# Send image info to logs
cat /usr/local/etc/image_info.txt

# Template variables
if [ "${DISABLE_INBOUND}" != "TRUE" ]; then
    # Inbound connections
    $(check_environment_vars INBOUND_LISTEN_HOSTNAME RECEIVING_CONTAINER_ADDRESS ) || exit 1
    $(check_environment_vars UKETS_SSL_CERTIFICATE UKETS_SSL_CERTIFICATE_KEY UKETS_SSL_CA) || exit 1

    echo "Configuring inbound..."
    echo "${UKETS_SSL_CERTIFICATE}" > /etc/ssl/certs/server.pem
    echo "${UKETS_SSL_CERTIFICATE_KEY}" > /etc/ssl/certs/server.key
    echo "${UKETS_SSL_CA}" > /etc/ssl/certs/client_ca.pem
else
    rm /etc/nginx/templates/inbound.conf.template
fi

if [ "${DISABLE_OUTBOUND}" != "TRUE" ]; then
    # Outbound connections
    $(check_environment_vars ITL_SSL_CERTIFICATE ITL_SSL_CERTIFICATE_KEY ITL_SSL_CA \
        OUTBOUND_LISTEN_PORT OUTBOUND_LISTEN_HOSTNAME ITL_HOSTNAME ITL_PORT ) || exit 1

    echo "Configuring outbound..."
    echo "${ITL_SSL_CERTIFICATE}" > /etc/nginx/client.pem
    echo "${ITL_SSL_CERTIFICATE_KEY}" > /etc/nginx/client.key
    echo "${ITL_SSL_CA}" > /etc/nginx/ca.crt
    echo "${ITL_IP} ${ITL_HOSTNAME}" >> /etc/hosts

    if [ "${INSTALL_ITL_SSL_CA}" == "TRUE" ]; then
        echo "Installing ITL CA..."
        mkdir -p /usr/share/ca-certificates/itl
        cp /etc/nginx/ca.crt /usr/share/ca-certificates/itl/itl.crt
        echo "itl/itl.crt" >> /etc/ca-certificates.conf
        update-ca-certificates
    fi
else
    rm /etc/nginx/templates/outbound.conf.template
fi

# Extra certs to install in the base image, e.g. during root certificate renewal
if [ "${INSTALL_EXTRA_CERTS}" != "FALSE" ]; then
        echo "Installing extra Certs..."
        mkdir -p /usr/share/ca-certificates/itl
        echo $INSTALL_EXTRA_CERTS > /usr/share/ca-certificates/itl/itl-extra.crt
        echo "itl/itl-etra.crt" >> /etc/ca-certificates.conf
        update-ca-certificate
fi
