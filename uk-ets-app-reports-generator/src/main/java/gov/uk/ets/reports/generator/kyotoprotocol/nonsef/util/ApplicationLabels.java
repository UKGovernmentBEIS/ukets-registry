package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ApplicationLabels {

    public static final String BUNDLE_NAME = "kyoto_protocol_labels";
    private static ResourceBundle rb;

    public static String get(String key) {
        if (rb == null) {
            rb = ResourceBundle.getBundle(BUNDLE_NAME);
        }
        String value = null;
        try {
            if (key != null) {
                value = rb.getString(key);
            }
        } catch (MissingResourceException e) {
            value = key;
        }
        return value;
    }

    public static String get(String key, Object... params) {
        if (rb == null) {
            rb = ResourceBundle.getBundle(BUNDLE_NAME);
        }
        return MessageFormat.format(rb.getString(key), params);
    }

    public static String getLabel(String label) {
        return get("label." + label);
    }
}
