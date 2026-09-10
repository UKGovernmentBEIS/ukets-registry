FROM nginx:1.30-alpine

RUN rm /etc/nginx/conf.d/default.conf /etc/nginx/nginx.conf

COPY confs/nginx.conf /etc/nginx/nginx.conf
COPY confs/page.conf /etc/nginx/conf.d/page.conf
COPY uk-ets-maintenance-page-web /usr/share/nginx/maintenance
