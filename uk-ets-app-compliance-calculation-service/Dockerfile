FROM eclipse-temurin:21-jre

RUN apt-get update \
    && apt-get install -y --no-install-recommends tzdata \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd -g 499 ets \
    && useradd -r -u 499 -g ets ets

USER ets
COPY target/uk-ets-app-compliance-calculation-service-env_version.jar /opt/applications/uk-ets-app-compliance-calculation-service/
WORKDIR /opt/applications/uk-ets-app-compliance-calculation-service/
CMD ["java", "-Dlog4j2.formatMsgNoLookups=true", "-XX:+PrintFlagsFinal", "-Xmx500m", "-jar", "uk-ets-app-compliance-calculation-service-env_version.jar"]
