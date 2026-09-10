FROM nginx:1.30-alpine

RUN rm /etc/nginx/conf.d/default.conf /etc/nginx/nginx.conf

COPY confs/nginx.conf /etc/nginx/nginx.conf
COPY confs/page.conf /etc/nginx/conf.d/page.conf

COPY uk-ets-cookies-page-web /usr/share/nginx/cookies
COPY uk-ets-cookies-page-web/resources /usr/share/nginx/resources
COPY uk-ets-cookies-page-web/resources /usr/share/nginx/assets
COPY uk-ets-cookies-page-web/resources/img /usr/share/nginx/assets/fonts
COPY uk-ets-cookies-page-web/resources/img /usr/share/nginx/assets/images
COPY uk-ets-cookies-page-web/resources /usr/share/nginx/cookies/
