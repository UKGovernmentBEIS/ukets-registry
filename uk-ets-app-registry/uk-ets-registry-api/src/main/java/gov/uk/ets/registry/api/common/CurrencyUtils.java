package gov.uk.ets.registry.api.common;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {

    private CurrencyUtils() {

    }
    
    /**
     * Formats the provided amount with 2 fraction digits for the UK locale.
     * 
     * @param amount the number to format
     * @return the formatted string
     */
    public static String prettyCurrency(BigDecimal amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.UK);
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        
        return formatter.format(amount);
    }
}
