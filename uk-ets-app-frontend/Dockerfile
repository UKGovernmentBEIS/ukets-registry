FROM nginx:1.28

COPY conf/nginx.conf /etc/nginx/nginx.conf
COPY conf/ets.conf /etc/nginx/conf.d/ets.conf
COPY conf/entrypoint.sh /usr/local/bin/entrypoint.sh
COPY conf/global_headers.conf /etc/nginx/global_headers.conf

ENTRYPOINT ["/bin/bash","/usr/local/bin/entrypoint.sh"]
