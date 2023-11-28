package gov.uk.ets.reports.generator.kyotoprotocol.sef.enums;

/**
 * @author gkountak
 *
 */
public enum ITLCommitmentPeriodEnum {
    CP(0), CP1(1), CP2(2), CP3(3);

    private int code;

    /**
     * @param code
     */
    private ITLCommitmentPeriodEnum(int code) {
        this.code = code;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    public static ITLCommitmentPeriodEnum getFromCode(int code) {
        for (ITLCommitmentPeriodEnum value : ITLCommitmentPeriodEnum.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

    public static ITLCommitmentPeriodEnum getFromName(String name) {
        if(name.equals("CP0")) {
            return CP;
        }
        if(name.equals("CP1")) {
            return CP1;
        }
        if(name.equals("CP2")) {
            return CP2;
        }
        if(name.equals("CP3")) {
            return CP3;
        }
        return null;
    }
}
