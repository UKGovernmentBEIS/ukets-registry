FROM nginx:1.30

COPY conf/nginx.conf /etc/nginx/nginx.conf
COPY conf/ets.conf /etc/nginx/conf.d/ets.conf
COPY conf/entrypoint.sh /usr/local/bin/entrypoint.sh
COPY conf/global_headers.conf /etc/nginx/global_headers.conf
COPY conf/postgrest.location /etc/nginx/conf.d/postgrest.location

ENTRYPOINT ["/bin/bash","/usr/local/bin/entrypoint.sh"]
