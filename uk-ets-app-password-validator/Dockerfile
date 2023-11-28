FROM eclipse-temurin:17-jdk
ENV http_proxy http://153.89.205.10:8080
ENV https_proxy http://153.89.205.10:8080
ENV no_proxy 172.17.54.4,172.17.54.6,git.trasys.gr
RUN apt-get update
RUN apt-get install -y tzdata
RUN cp /usr/share/zoneinfo/Europe/London /etc/localtime
RUN echo "Europe/London" >  /etc/timezone
RUN groupadd -g 999 ets && \
    useradd -r -u 999 -g ets ets
USER ets
COPY target/uk-ets-app-password-validator-env_version.jar /opt/applications/uk-ets-app-password-validator-test/
WORKDIR /opt/applications/uk-ets-app-password-validator-test/
CMD ["java", "-XX:+PrintFlagsFinal", "-Xmx100m", "-jar", "uk-ets-app-password-validator-env_version.jar"]
