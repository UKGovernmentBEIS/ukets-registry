package gov.uk.ets.ui.logs;

import static org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX;

import java.io.IOException;
import java.nio.file.Files;
import org.springframework.util.ResourceUtils;

/**
 * @author Chris Georgoulis on 11/11/2021.
 */
public final class UiLogsUtils {

    private UiLogsUtils() {
        throw new UnsupportedOperationException();
    }

    public static final String UI_LOGS_URL = "/api-ui-logs/logs.submit";

    public static final String LOG_SAMPLES_LOCATION = CLASSPATH_URL_PREFIX + "json-samples/";
    public static final String WELL_FORMED_LOG_SAMPLES_LOCATION =
        LOG_SAMPLES_LOCATION + "well-formed-log-samples.json";
    public static final String SANITIZABLE_BAD_LOG_SAMPLES_LOCATION =
        LOG_SAMPLES_LOCATION + "sanitizable-bad-log-samples.json";
    public static final String EXPECTED_SANITIZED_BAD_LOG_SAMPLES_LOCATION =
        LOG_SAMPLES_LOCATION + "expected-sanitized-samples.json";
    public static final String NON_SANITIZABLE_LOG_SAMPLES_LOCATION =
        LOG_SAMPLES_LOCATION + "non-sanitizable-log-samples.json";

    public static String readBadlyFormedSanitizableJson() throws IOException {
        return readJson(SANITIZABLE_BAD_LOG_SAMPLES_LOCATION);
    }

    public static String readExpectedSanitizedJson() throws IOException {
        return readJson(EXPECTED_SANITIZED_BAD_LOG_SAMPLES_LOCATION);
    }

    public static String readWellFormedJson() throws IOException {
        return readJson(WELL_FORMED_LOG_SAMPLES_LOCATION);
    }

    public static String readNonSanitizableJson() throws IOException {
        return readJson(NON_SANITIZABLE_LOG_SAMPLES_LOCATION);
    }


    public static String readJson(String location) throws IOException {
        return Files.readString(ResourceUtils.getFile(location).toPath());
    }
}
