FROM nginx:1.24
RUN apt-get update
RUN apt-get install -y apache2-utils
RUN rm /etc/nginx/conf.d/default.conf
RUN rm /etc/nginx/nginx.conf
COPY nginx/ /etc/nginx/
COPY docker-entrypoint.d/ /docker-entrypoint.d/
RUN apt-get install -y procps netcat vim net-tools iputils-ping dnsutils bind9-host net-tools tcpdump
RUN echo "ITL-Proxy Image build date $(date +%Y%m%d)" > /usr/local/etc/image_info.txt
RUN echo "alias ll='ls -alF'" >> /root/.bashrc
