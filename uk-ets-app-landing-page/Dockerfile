FROM nginx:1.19.0
ENV XAUTH $HOME/.Xauthority

#RUN export 'http_proxy=http://153.89.205.10:8080' ;apt-get update
RUN apt-get update
RUN apt-get install -y apache2-utils

RUN rm /etc/nginx/conf.d/default.conf
RUN rm /etc/nginx/nginx.conf
COPY confs/nginx.conf /etc/nginx/nginx.conf
COPY confs/htpasswd.users /etc/nginx/htpasswd.users
COPY confs/page.conf /etc/nginx/conf.d/page.conf

# set global resources
COPY resources /usr/share/nginx/resources
COPY resources /usr/share/nginx/assets
COPY resources/css /usr/share/nginx/css
COPY resources/img /usr/share/nginx/img
COPY resources/js /usr/share/nginx/js
COPY resources/img /usr/share/nginx/assets/fonts
COPY resources/img /usr/share/nginx/assets/images

# serving /
COPY uk-ets-landing-page-web /usr/share/nginx

# serving /landing/
COPY uk-ets-landing-page-web /usr/share/nginx/landing
COPY resources/css /usr/share/nginx/landing/css
COPY resources/img /usr/share/nginx/landing/img
COPY resources/js /usr/share/nginx/landing/js

# serving /privacy/
COPY uk-ets-privacy-page-web /usr/share/nginx/privacy
COPY resources/css /usr/share/nginx/privacy/css
COPY resources/img /usr/share/nginx/privacy/img
COPY resources/js /usr/share/nginx/privacy/js

# serving /accessibility/
COPY uk-ets-accessibility-page-web /usr/share/nginx/accessibility
COPY resources/css /usr/share/nginx/accessibility/css
COPY resources/img /usr/share/nginx/accessibility/img
COPY resources/js /usr/share/nginx/accessibility/js

# serving /contact-us/
COPY uk-ets-contact-page-web /usr/share/nginx/contact-us
COPY resources/css /usr/share/nginx/contact/css
COPY resources/img /usr/share/nginx/contact/img
COPY resources/js /usr/share/nginx/contact/js

# serving /ets-reports/
COPY uk-ets-report-page-web /usr/share/nginx/ets-reports
COPY resources/css /usr/share/nginx/ets-reports/css
COPY resources/img /usr/share/nginx/ets-reports/img
COPY resources/js /usr/share/nginx/ets-reports/js
COPY resources/reports /usr/share/nginx/ets-reports/reports

# serving /kp-reports/
COPY uk-ets-contact-page-web /usr/share/nginx/kp-reports
COPY resources/css /usr/share/nginx/kp-reports/css
COPY resources/img /usr/share/nginx/kp-reports/img
COPY resources/js /usr/share/nginx/kp-reports/js
COPY resources/reports /usr/share/nginx/kp-reports/reports
