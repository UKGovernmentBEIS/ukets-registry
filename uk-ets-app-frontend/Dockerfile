FROM nginx:1.30-alpine

COPY conf/nginx.conf conf/global_headers.conf /etc/nginx/
COPY conf/ets.conf conf/postgrest.location /etc/nginx/conf.d/
COPY conf/entrypoint.sh /usr/local/bin/entrypoint.sh

ENTRYPOINT ["/bin/sh","/usr/local/bin/entrypoint.sh"]
