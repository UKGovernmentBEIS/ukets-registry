FROM nginx:1.19.0
ENV XAUTH $HOME/.Xauthority

##RUN export 'http_proxy=http://153.89.205.10:8080' ;apt-get update
RUN apt-get update
RUN apt-get install -y apache2-utils
RUN rm /etc/nginx/conf.d/default.conf
RUN rm /etc/nginx/nginx.conf
COPY confs/nginx.conf /etc/nginx/nginx.conf
COPY confs/page.conf /etc/nginx/conf.d/page.conf

COPY uk-ets-app-digital-dashboard-web/resources /usr/share/nginx/resources
COPY uk-ets-app-digital-dashboard-web/resources /usr/share/nginx/assets
COPY uk-ets-app-digital-dashboard-web/resources/img /usr/share/nginx/assets/fonts
COPY uk-ets-app-digital-dashboard-web/resources/img /usr/share/nginx/assets/images
COPY uk-ets-app-digital-dashboard-web/resources/css /usr/share/nginx/cookies/css
COPY uk-ets-app-digital-dashboard-web/resources/img /usr/share/nginx/cookies/img
COPY uk-ets-app-digital-dashboard-web/resources/js /usr/share/nginx/cookies/js
