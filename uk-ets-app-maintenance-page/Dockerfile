FROM nginx:1.28
ENV XAUTH $HOME/.Xauthority

##RUN export 'http_proxy=http://153.89.205.10:8080' ;apt-get update
RUN apt-get update
RUN apt-get install -y apache2-utils
RUN rm /etc/nginx/conf.d/default.conf
RUN rm /etc/nginx/nginx.conf
COPY confs/nginx.conf /etc/nginx/nginx.conf
COPY confs/page.conf /etc/nginx/conf.d/page.conf

COPY uk-ets-maintenance-page-web /usr/share/nginx/maintenance