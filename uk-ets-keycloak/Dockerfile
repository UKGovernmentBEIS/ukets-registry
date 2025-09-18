FROM registry.access.redhat.com/ubi9 AS ubi-micro-build
RUN mkdir -p /mnt/rootfs
RUN ulimit -n 1024 && dnf install --installroot /mnt/rootfs curl jq --releasever 9 --setopt install_weak_deps=false --nodocs -y; dnf --installroot /mnt/rootfs clean all

FROM quay.io/keycloak/keycloak:26.1.0
COPY --from=ubi-micro-build /mnt/rootfs /

USER root

# Custom jars
COPY uk-ets-keycloak-logs-producer/target/uk-ets-keycloak-logs-producer-env_version.jar /opt/keycloak/providers/
COPY uk-ets-keycloak-scripts/target/uk-ets-keycloak-scripts-env_version.jar /opt/keycloak/providers/
COPY uk-ets-keycloak-theme/target/uk-ets-keycloak-theme-env_version.jar /opt/keycloak/providers/
COPY uk-ets-keycloak-users-service/target/com.mysema.commons.mysema-commons-lang.jar /opt/keycloak/providers/
COPY uk-ets-keycloak-users-service/target/com.querydsl.querydsl-core.jar /opt/keycloak/providers/
COPY uk-ets-keycloak-users-service/target/com.querydsl.querydsl-jpa.jar /opt/keycloak/providers/
COPY uk-ets-keycloak-users-service/target/uk-ets-keycloak-users-service-env_version.jar /opt/keycloak/providers/
COPY uk-ets-keycloak-otp-validator/target/uk-ets-keycloak-otp-validator-env_version.jar /opt/keycloak/providers/
COPY uk-ets-keycloak-event-producer/target/uk-ets-keycloak-event-producer-env_version.jar /opt/keycloak/providers/
COPY uk-ets-keycloak-password-policy/target/uk-ets-keycloak-password-policy-env_version.jar /opt/keycloak/providers/
COPY uk-ets-keycloak-authenticator/target/uk-ets-keycloak-authenticator-env_version.jar /opt/keycloak/providers/
COPY uk-ets-keycloak-password-validator/target/uk-ets-keycloak-password-validator-env_version.jar /opt/keycloak/providers/
COPY uk-ets-keycloak-migration-manager/target/uk-ets-keycloak-migration-manager-env_version.jar /opt/keycloak/providers/
COPY uk-ets-keycloak-code-service/target/uk-ets-keycloak-code-service-env_version.jar /opt/keycloak/providers/


ENV KC_DB=postgres
ENV KC_HTTP_RELATIVE_PATH=/auth
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true
RUN /opt/keycloak/bin/kc.sh build

COPY default-realm-config/default-setup.sh /opt/keycloak/tools/
COPY default-realm-config/uk-ets-realm-base.json /opt/keycloak/tools/
COPY default-realm-config/uk-ets-realm-sample-users.json /opt/keycloak/tools/
COPY default-realm-config/uk-ets-realm-dev-sample-users.json /opt/keycloak/tools/
COPY default-realm-config/uk-ets-realm-prod-sample-users.json /opt/keycloak/tools/
COPY default-realm-config/docker-entrypoint.sh /opt/keycloak/tools/
COPY default-realm-config/property-setter.cli /opt/keycloak/tools
# copy entire migrations folder:
COPY default-realm-config/migrations/ /opt/keycloak/tools/migrations/
# migrations scripts need write access to the migration json files:
RUN chown -R 1000:root /opt/keycloak/tools/

USER 1000
ENTRYPOINT ["/opt/keycloak/tools/docker-entrypoint.sh"]