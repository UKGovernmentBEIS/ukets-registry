FROM nginx:1.30-alpine

RUN rm /etc/nginx/conf.d/default.conf /etc/nginx/nginx.conf

COPY confs/nginx.conf /etc/nginx/nginx.conf
COPY confs/page.conf /etc/nginx/conf.d/page.conf

# default main page
COPY uk-ets-app-digital-dashboard-web /usr/share/nginx/digital-dashboard
COPY uk-ets-app-digital-dashboard-web/resources /usr/share/nginx/digital-dashboard

# survey page to serve directly
COPY uk-ets-app-digital-dashboard-web /usr/share/nginx/digital-survey
COPY uk-ets-app-digital-dashboard-web/resources /usr/share/nginx/digital-survey
COPY uk-ets-app-digital-dashboard-web/src/app/survey.html /usr/share/nginx/digital-survey/src/app/main.html

# confirmation page to serve directly
COPY uk-ets-app-digital-dashboard-web /usr/share/nginx/digital-confirmation
COPY uk-ets-app-digital-dashboard-web/resources /usr/share/nginx/digital-confirmation
COPY uk-ets-app-digital-dashboard-web/src/app/confirmation.html /usr/share/nginx/digital-confirmation/src/app/main.html

# error page to serve directly
COPY uk-ets-app-digital-dashboard-web /usr/share/nginx/digital-error
COPY uk-ets-app-digital-dashboard-web/resources /usr/share/nginx/digital-error
COPY uk-ets-app-digital-dashboard-web/src/app/error.html /usr/share/nginx/digital-error/src/app/main.html

# css,fonts,etc
COPY uk-ets-app-digital-dashboard-web/resources /usr/share/nginx/resources
COPY uk-ets-app-digital-dashboard-web/resources /usr/share/nginx/assets
COPY uk-ets-app-digital-dashboard-web/resources/img /usr/share/nginx/assets/fonts
COPY uk-ets-app-digital-dashboard-web/resources/img /usr/share/nginx/assets/images
COPY uk-ets-app-digital-dashboard-web/resources /usr/share/nginx/cookies
