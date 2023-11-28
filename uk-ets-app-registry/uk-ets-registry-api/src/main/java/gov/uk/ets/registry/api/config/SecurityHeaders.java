package gov.uk.ets.registry.api.config;

import static java.util.Map.entry;
import static java.util.stream.Collectors.joining;

import java.util.Map;

public class SecurityHeaders {
    public static final String CSP_HEADER;
    public static final String FEATURE_POLICY_HEADER;

    private static final String SELF = "'self'";
    private static final String NONE = "'none'";
    private static final String UNSAFE_INLINE = "'unsafe-inline'";
    public static final String BLACKLIST_SERVICE_URL = "api.pwnedpasswords.com";
    public static final String GOOGLE_TAG_MANAGER_URL = "https://www.googletagmanager.com";
    public static final String GOOGLE_ANALYTICS_URL = "https://www.google-analytics.com https://ssl.google-analytics.com https://region1.google-analytics.com";
    public static final String YOUTUBE_URL = "https://www.youtube.com";

    private static final Map<String, String> CSP_HEADERS_MAP = Map.of(
        "default-src", NONE,
        "base-uri", SELF,
        "script-src", combine(SELF, GOOGLE_TAG_MANAGER_URL, GOOGLE_ANALYTICS_URL),
        "img-src", combine(SELF, GOOGLE_TAG_MANAGER_URL, GOOGLE_ANALYTICS_URL),
        // TODO to remove the unsafe-inline directive all inline style must be moved to separate css files,
        //  but also Angular seems to inject css inline anyway so this must be investigated see this open issue:
        // https://github.com/angular/angular/issues/26152
        "style-src", combine(SELF, UNSAFE_INLINE),
        "font-src", SELF,
        "connect-src", combine(SELF,  BLACKLIST_SERVICE_URL, GOOGLE_TAG_MANAGER_URL, GOOGLE_ANALYTICS_URL),
        "form-action", SELF,
        "frame-src", combine(SELF, YOUTUBE_URL),
        "object-src", combine(SELF, YOUTUBE_URL)

    );

    // omitted  features that are not supported by Chrome
    private static final Map<String, String> FEATURE_POLICY_HEADERS_MAP = Map.ofEntries(
        entry("accelerometer", NONE),
        entry("autoplay", NONE),
        entry("camera", NONE),
        entry("document-domain", NONE),
        entry("encrypted-media", NONE),
        entry("fullscreen", NONE),
        entry("geolocation", NONE),
        entry("gyroscope", NONE),
        entry("magnetometer", NONE),
        entry("microphone", NONE),
        entry("midi", NONE),
        entry("payment", NONE),
        entry("picture-in-picture", NONE),
        entry("publickey-credentials-get", NONE),
        entry("sync-xhr", NONE),
        entry("usb", NONE),
        entry("screen-wake-lock", NONE),
        entry("xr-spatial-tracking", NONE)
    );

    static {
        CSP_HEADER = getHeaderAsString(CSP_HEADERS_MAP);
        FEATURE_POLICY_HEADER = getHeaderAsString(FEATURE_POLICY_HEADERS_MAP);
    }

    private static String getHeaderAsString(Map<String, String> headerMap) {
        return headerMap.entrySet().stream()
            .map(entry -> combine(entry.getKey(), entry.getValue()))
            .collect(joining("; "));
    }

    private static String combine(String... values) {
        return String.join(" ", values);
    }
}
