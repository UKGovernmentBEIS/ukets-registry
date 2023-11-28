FROM eclipse-temurin:17-jdk
RUN apt-get update
RUN apt-get install -y tzdata
RUN groupadd -g 499 ets && \
    useradd -r -u 499 -g ets ets
USER ets
COPY uk-ets-itl-receiving-endpoint/target/uk-ets-itl-receiving-endpoint-env_version.jar /opt/applications/uk-ets-app-itl-receiving-endpoint/
COPY uk-ets-itl-receiving-endpoint/target/webapps/itl-ws.war /opt/applications/uk-ets-app-itl-receiving-endpoint/target/webapps/
WORKDIR /opt/applications/uk-ets-app-itl-receiving-endpoint/
CMD ["java", "-XX:+PrintFlagsFinal", "-Xmx100m", "-jar", "uk-ets-itl-receiving-endpoint-env_version.jar", "-Dlog4j2.formatMsgNoLookups=true"]
