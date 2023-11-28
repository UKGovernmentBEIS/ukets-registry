FROM eclipse-temurin:17-jdk
RUN apt-get update
RUN apt-get install -y tzdata
RUN groupadd -g 499 ets && \
    useradd -r -u 499 -g ets ets
USER ets
COPY target/uk-ets-app-reports-generator-env_version.jar /opt/applications/uk-ets-app-reports-generator/
WORKDIR /opt/applications/uk-ets-app-reports-generator/
CMD ["java", "-Dlog4j2.formatMsgNoLookups=true", "-XX:+PrintFlagsFinal", "-Xmx2048m", "-jar", "uk-ets-app-reports-generator-env_version.jar"]
