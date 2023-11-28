package gov.uk.ets.reports.generator.kyotoprotocol.sef.enums;

/**
 * @author gkountak
 *
 */
public enum ITLLulucfTypeEnum {

    AFFO_REFO(1), DEFORESTATION(2), FOREST_MANAGEMENT(3), CROPLAND_MANAGEMENT(4), GRAZING_LAND_MANAGEMENT(5), REVEGETATION(
            6), WETLAND_DRAINAGE_REWETTING(7);

    private int code;

    /**
     * @param code
     */
    private ITLLulucfTypeEnum(int code) {
        this.code = code;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    public static ITLLulucfTypeEnum getFromCode(String code) {
        if ("AFFORESTATION_AND_REFORESTATION".equals(code)) {
            return AFFO_REFO;
        }
        if ("DEFORESTATION".equals(code)) {
            return DEFORESTATION;
        }
        if ("FOREST_MANAGEMENT".equals(code)) {
            return FOREST_MANAGEMENT;
        }
        if ("CROPLAND_MANAGEMENT".equals(code)) {
            return CROPLAND_MANAGEMENT;
        }
        if ("GRAZING_LAND_MANAGEMENT".equals(code)) {
            return GRAZING_LAND_MANAGEMENT;
        }
        if ("REVEGETATION".equals(code)) {
            return REVEGETATION;
        }
        if ("WETLAND_DRAINAGE_AND_REWETTING".equals(code)) {
            return WETLAND_DRAINAGE_REWETTING;
        }
        return null;
    }
}
