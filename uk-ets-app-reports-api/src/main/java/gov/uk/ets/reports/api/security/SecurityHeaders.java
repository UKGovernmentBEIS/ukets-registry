package gov.uk.ets.reports.api.security;

import static java.util.Map.entry;
import static java.util.stream.Collectors.joining;

import java.util.Map;

public class SecurityHeaders {

    private SecurityHeaders() {
    }

    public static final String CSP_HEADER;
    public static final String FEATURE_POLICY_HEADER;

    private static final String SELF = "'self'";
    private static final String NONE = "'none'";
    private static final String UNSAFE_INLINE = "'unsafe-inline'";

    private static final Map<String, String> CSP_HEADERS_MAP = Map.of(
        "default-src", NONE,
        "base-uri", SELF,
        "script-src", SELF,
        "img-src", SELF,
        // TODO to remove the unsafe-inline directive all inline style must be moved to separate css files,
        //  but also Angular seems to inject css inline anyway so this must be investigated see this open issue:
        // https://github.com/angular/angular/issues/26152
        "style-src", combine(SELF, UNSAFE_INLINE),
        "font-src", SELF,
        "connect-src", SELF,
        "form-action", SELF,
        "frame-src", SELF

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
