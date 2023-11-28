FROM eclipse-temurin:17-jdk
RUN apt-get update
RUN apt-get install -y tzdata
RUN groupadd -g 499 ets && \
    useradd -r -u 499 -g ets ets
USER ets
COPY uk-ets-registry-api/target/uk-ets-registry-api-env_version.jar /opt/applications/uk-ets-registry-api/
WORKDIR /opt/applications/uk-ets-registry-api/
CMD ["java", "-Dlog4j2.formatMsgNoLookups=true", "-XX:+PrintFlagsFinal", "-Xmx1024m", "-jar", "uk-ets-registry-api-env_version.jar"]
