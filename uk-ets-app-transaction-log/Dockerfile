FROM eclipse-temurin:17-jdk
RUN apt-get update
RUN apt-get install -y tzdata
RUN groupadd -g 499 ets && \
    useradd -r -u 499 -g ets ets
USER ets
COPY target/uk-ets-app-transaction-log-env_version.jar /opt/applications/uk-ets-app-transaction-log/
WORKDIR /opt/applications/uk-ets-app-transaction-log/
CMD ["java", "-XX:+PrintFlagsFinal", "-Xmx100m", "-jar", "-Dlog4j2.formatMsgNoLookups=true", "uk-ets-app-transaction-log-env_version.jar"]
