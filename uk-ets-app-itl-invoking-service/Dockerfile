FROM eclipse-temurin:17-jdk
RUN apt-get update
RUN apt-get install -y tzdata
RUN groupadd -g 499 ets && \
    useradd -r -u 499 -g ets ets
USER ets
COPY target/uk-ets-app-itl-invoking-service-env_version.jar /opt/applications/uk-ets-app-itl-invoking-service/
WORKDIR /opt/applications/uk-ets-app-itl-invoking-service/
CMD ["java", "-XX:+PrintFlagsFinal","-Xmx100m", "-jar", "uk-ets-app-itl-invoking-service-env_version.jar", "-Dlog4j2.formatMsgNoLookups=true"]
