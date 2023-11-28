package gov.uk.ets.publication.api;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Locale;

public class Utils {

    /**
     * Converts an amount of bytes to a human-readable form.
     * For example 450 B, 20 kB, 10 MB etc.
     *
     * @param amount The amount of bytes
     * @return a human-readable string
     */
    public static String convertByteAmountToHumanReadable(long amount) {
        long absoluteBytes = amount == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(amount);
        if (absoluteBytes < 1024) {
            return amount + " B";
        }
        long value = absoluteBytes;
        CharacterIterator iterator = new StringCharacterIterator("kMGTPE");
        for (int index = 40; index >= 0 && absoluteBytes > 0xfffccccccccccccL >> index; index -= 10) {
            value >>= 10;
            iterator.next();
        }
        value *= Long.signum(amount);
        return String.format(Locale.ENGLISH, "%.1f %cB", value / 1024.0, iterator.current());
    }
}
