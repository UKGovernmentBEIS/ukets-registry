FROM nginx:1.30-alpine

RUN rm /etc/nginx/conf.d/default.conf /etc/nginx/nginx.conf

COPY confs/nginx.conf /etc/nginx/nginx.conf
COPY confs/page.conf /etc/nginx/conf.d/page.conf

COPY uk-ets-error-page-web /usr/share/nginx/errorpage
COPY uk-ets-error-page-web/resources /usr/share/nginx/resources
COPY uk-ets-error-page-web/resources /usr/share/nginx/assets
COPY uk-ets-error-page-web/resources/img /usr/share/nginx/assets/fonts
COPY uk-ets-error-page-web/resources/img /usr/share/nginx/assets/images
COPY uk-ets-error-page-web/resources/css /usr/share/nginx/errorpage/css
COPY uk-ets-error-page-web/resources/img /usr/share/nginx/errorpage/img
COPY uk-ets-error-page-web/resources/js /usr/share/nginx/errorpage/js
