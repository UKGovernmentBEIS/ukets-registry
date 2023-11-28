package gov.uk.ets.registry.api.transaction.domain.type;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Enumerates the various LULUCF environmental activities.
 */
public enum EnvironmentalActivity {

    /**
     * Afforestation and reforestation.
     */
    AFFORESTATION_AND_REFORESTATION("AR", 1),

    /**
     * Deforestation.
     */
    DEFORESTATION("D", 2),

    /**
     * Forest management.
     */
    FOREST_MANAGEMENT("FM", 3),

    /**
     * Cropland management.
     */
    CROPLAND_MANAGEMENT("CM", 4),

    /**
     * Grazing land management.
     */
    GRAZING_LAND_MANAGEMENT("GM", 5),

    /**
     * Revegetation.
     */
    REVEGETATION("RV", 6),

    /**
     * Wetland drainage and rewetting.
     */
    WETLAND_DRAINAGE_AND_REWETTING("WDR", 7);

    /**
     * The abbreviation.
     */
    private String abbreviation;

    /**
     * The code.
     */
    private Integer code;

    /**
     * Constructor.
     * @param abbreviation The abbreviation.
     * @param code The code.
     */
    EnvironmentalActivity(String abbreviation, Integer code) {
        this.abbreviation = abbreviation;
        this.code = code;
    }

    /**
     * Returns the code.
     * @return the code.
     */
    public Integer getCode() {
        return code;
    }

    /**
     * Returns the abbreviation.
     * @return the abbreviation.
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Identifies the environmental activity based on the code.
     * @param code The code.
     * @return an environmental activity.
     */
    public static EnvironmentalActivity of(int code) {
        EnvironmentalActivity result = null;
        Optional<EnvironmentalActivity> optional = Stream.of(values())
            .filter(type -> type.getCode().equals(code))
            .findFirst();
        if (optional.isPresent()) {
            result = optional.get();
        }
        return result;
    }
}
