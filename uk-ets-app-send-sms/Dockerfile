FROM eclipse-temurin:17-jdk
RUN apt-get update
RUN apt-get install -y tzdata
RUN groupadd -g 499 ets && \
    useradd -r -u 499 -g ets ets
USER ets
COPY target/uk-ets-app-send-sms-env_version.jar /opt/applications/uk-ets-app-send-sms/
WORKDIR /opt/applications/uk-ets-app-send-sms/
CMD ["java", "-XX:+PrintFlagsFinal", "-Xmx512m", "-jar", "-Dlog4j2.formatMsgNoLookups=true", "uk-ets-app-send-sms-env_version.jar"]
