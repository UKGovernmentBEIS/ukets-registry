FROM nginx:1.30-alpine

RUN rm /etc/nginx/conf.d/default.conf /etc/nginx/nginx.conf

COPY confs/nginx.conf /etc/nginx/nginx.conf
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
COPY resources /usr/share/nginx/landing

# serving /privacy/
COPY uk-ets-privacy-page-web /usr/share/nginx/privacy
COPY resources /usr/share/nginx/privacy

# serving /accessibility/
COPY uk-ets-accessibility-page-web /usr/share/nginx/accessibility
COPY resources /usr/share/nginx/accessibility

# serving /contact-us/
COPY uk-ets-contact-page-web /usr/share/nginx/contact-us
COPY resources /usr/share/nginx/contact
