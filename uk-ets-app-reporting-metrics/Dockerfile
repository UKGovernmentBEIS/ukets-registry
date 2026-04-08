FROM eclipse-temurin:21-jdk
RUN apt-get update
RUN apt-get install -y tzdata
RUN groupadd -g 499 ets && \
    useradd -r -u 499 -g ets ets
USER ets
COPY target/uk-ets-app-reporting-metrics-env_version.jar /opt/applications/uk-ets-app-reporting-metrics/
WORKDIR /opt/applications/uk-ets-app-reporting-metrics/
CMD ["java", "-Dlog4j2.formatMsgNoLookups=true", "-XX:+PrintFlagsFinal", "-Xmx256m", "-jar", "uk-ets-app-reporting-metrics-env_version.jar"]
