package gov.uk.ets.reports.generator.kyotoprotocol.sef.enums;

/**
 * @author gkountak
 *
 */
public enum ITLUnitTypeEnum {

    AAU(1, "AAU"), RMU(2, "RMU"), ERU_FROM_AAU(3, "ERU"), ERU_FROM_RMU(4, "ERU"), CER(5, "CER"), TCER(6, "tCER"), LCER(
            7, "lCER");

    private int code;
    private String description;

    /**
     *
     * @param ordinal
     * @param desc
     */
    private ITLUnitTypeEnum(int ordinal, String desc) {
        code = ordinal;
        description = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public ITLUnitTypeEnum getPreConvertedType() {
        if (this.equals(ERU_FROM_AAU)) {
            return AAU;
        }

        if (this.equals(ERU_FROM_RMU)) {
            return RMU;
        }
        return null;
    }

    public static ITLUnitTypeEnum getFromCode(int code) {
        for (ITLUnitTypeEnum value : ITLUnitTypeEnum.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

    public static ITLUnitTypeEnum getFromName(String name) {
        if ("FORMER_EUA".equals(name)) {
            return AAU;
        }

        for (ITLUnitTypeEnum value : ITLUnitTypeEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
