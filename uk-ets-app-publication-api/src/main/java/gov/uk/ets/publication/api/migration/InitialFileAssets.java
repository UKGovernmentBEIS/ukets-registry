package gov.uk.ets.publication.api.migration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InitialFileAssets {

    COOKIES_CSS("assets/css/cookies.css","text/css"),
    GOVUK_CSS("assets/css/govuk-frontend-3.9.1.min.css","text/css"),
    STYLES_CSS("assets/css/styles.css","text/css"),
    COOKIES_JS("assets/js/cookies.js","text/javascript"),
    GOVUK_JS("assets/js/gov.uk-frontend-3.9.1.min.js","text/javascript"),
    SECTIONS_JS("assets/js/sections.js","text/javascript"),
    FAVICON_ICO("assets/images/favicon.ico","image/x-icon"),
    APPLE_TOUCH_PNG("assets/images/govuk-apple-touch-icon.png","image/png"),
    APPLE_TOUCH_152_PNG("assets/images/govuk-apple-touch-icon-152x152.png","image/png"),
    APPLE_TOUCH_167_PNG("assets/images/govuk-apple-touch-icon-167x167.png","image/png"),
    APPLE_TOUCH_180_PNG("assets/images/govuk-apple-touch-icon-180x180.png","image/png"),
    CREST_PNG("assets/images/govuk-crest.png","image/png"),
    CREST_2X_PNG("assets/images/govuk-crest-2x.png","image/png"),
    OPENGRAPH_PNG("assets/images/govuk-opengraph-image.png","image/png"),
    ICON_FILE_PNG("assets/images/icon-file-download.png","image/png"),
    MASK_SVG("assets/images/govuk-mask-icon.svg","image/svg+xml"),
    MASK_COPY_SVG("assets/images/govuk-mask-icon copy.svg","image/svg+xml"),
    INDEX_HTML("index.html","text/html");

    private final String filePath;
    private final String contentType;
}