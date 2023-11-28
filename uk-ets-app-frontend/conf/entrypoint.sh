#!/bin/bash

rm -f /etc/nginx/conf.d/default.conf

if [[ $ORCHESTRATOR == 'ecs' ]]; then
	sed -i '/listen/i resolver\ 169\.254\.169\.253\ valid\=10s\;' /etc/nginx/conf.d/ets.conf
elif [[ $ORCHESTRATOR == 'swarm' ]]; then
        sed -i '/listen/i resolver\ 127\.0\.0\.11\ valid\=10s\;' /etc/nginx/conf.d/ets.conf
fi

sed -i "s/REPLACE_ME_WITH_FQDN/$FQDN/g" /etc/nginx/conf.d/ets.conf
sed -i "s/REPLACE_ME_WITH_KEYCLOAK_FQDN/$KEYCLOAK/g" /etc/nginx/conf.d/ets.conf
sed -i "s/REPLACE_ME_WITH_REGISTRATION_FQDN/$REGISTRATION/g" /etc/nginx/conf.d/ets.conf
sed -i "s/REPLACE_ME_WITH_RECEIVING_FQDN/$RECEIVING/g" /etc/nginx/conf.d/ets.conf
sed -i "s/REPLACE_ME_WITH_PASS_VALIDATOR_FQDN/$VALIDATOR/g" /etc/nginx/conf.d/ets.conf
sed -i "s/REPLACE_ME_WITH_REPORTS_API_FQDN/$REPORTS/g" /etc/nginx/conf.d/ets.conf
sed -i "s/REPLACE_ME_WITH_PUBLICATION_API_FQDN/$PUBLICATION/g" /etc/nginx/conf.d/ets.conf
sed -i "s/REPLACE_ME_WITH_VAULT_FQDN/$VAULT/g" /etc/nginx/conf.d/ets.conf
sed -i "s/REPLACE_ME_WITH_COOKIES_FQDN/$COOKIES/g" /etc/nginx/conf.d/ets.conf
sed -i "s/REPLACE_ME_WITH_SIGNING_FQDN/$SIGNING/g" /etc/nginx/conf.d/ets.conf
sed -i "s/REPLACE_ME_WITH_REGISTRY_FQDN/$REGISTRY/g" /etc/nginx/conf.d/ets.conf
sed -i "s/REPLACE_ME_WITH_MAINTENANCE_FQDN/$MAINTENANCE/g" /etc/nginx/conf.d/ets.conf
sed -i "s/REPLACE_ME_WITH_DIGITAL_DASHBOARD_FQDN/$DASHBOARD/g" /etc/nginx/conf.d/ets.conf
sed -i "s/REPLACE_ME_WITH_FEEDBACK_FQDN/$FEEDBACK/g" /etc/nginx/conf.d/ets.conf
sed -i "s/REPLACE_ME_WITH_LANDING_FQDN/$LANDING/g" /etc/nginx/conf.d/ets.conf
sed -i "s/REPLACE_ME_WITH_ERROR_FQDN/$ERROR/g" /etc/nginx/conf.d/ets.conf
sed -i "s/REPLACE_ME_WITH_UILOGS_FQDN/$UILOGS/g" /etc/nginx/conf.d/ets.conf

exec nginx -g 'daemon off;'
