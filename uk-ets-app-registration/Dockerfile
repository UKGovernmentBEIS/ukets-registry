FROM eclipse-temurin:17-jdk
RUN apt-get update
RUN apt-get install -y tzdata
RUN groupadd -g 499 ets && \
    useradd -r -u 499 -g ets ets
USER ets
COPY uk-ets-registration-user-api/target/uk-ets-registration-user-api-env_version.jar /opt/applications/uk-ets-registration-user-api/
WORKDIR /opt/applications/uk-ets-registration-user-api/
CMD ["java", "-Dlog4j2.formatMsgNoLookups=true", "-XX:+PrintFlagsFinal", "-Xmx100m", "-jar", "uk-ets-registration-user-api-env_version.jar"]
