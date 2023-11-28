package gov.uk.ets.reports.generator.kyotoprotocol.sef.util;

import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.QuantityValueEnum;

/**
 * @author gkountak
 *
 */
public class Quantity {

    private static String NO = QuantityValueEnum.NO.name();
    private static String NA = QuantityValueEnum.NA.name();

    private String value = NO;

    public Quantity() {

    }

    /**
     * @param value
     */
    public Quantity(String value) {
        super();
        this.updateValue(value);
    }

    public String updateValue(String amount) {

        if (NA.equals(amount)) {
            this.value = NA;
            return value;
        }

        if (amount == null || NO.equals(amount)) {
            return value;
        }

        try {
            updateValue(Long.valueOf(amount));
        } catch (NumberFormatException e) {
            // ignore the input parameter amount
        }
        return value;
    }

    /**
     * Extra care must be taken for the 'value' field to never be 0.
     *
     * @param amount
     * @return
     */
    public String updateValue(Long amount) {
        long longValue = 0;

        if (NO.equals(value)) {
            longValue = 0;
        } else {
            longValue = Long.valueOf(value);
        }

        if (amount != null) {
            longValue += amount;
        }

        if (longValue == 0) {
            value = NO;
        } else {
            value = String.valueOf(longValue);
        }

        return value;
    }

    public String getValue() {
        return value;
    }

    public boolean isNO() {
        return NO.equals(value);
    }

    public long getNumericValue() {
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            // it was NA or NO
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Quantity other = (Quantity) obj;
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Quantity [value=" + value + "]\n";
    }

}
