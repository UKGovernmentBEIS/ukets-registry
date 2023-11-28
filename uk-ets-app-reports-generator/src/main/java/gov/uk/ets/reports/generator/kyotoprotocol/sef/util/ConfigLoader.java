package gov.uk.ets.reports.generator.kyotoprotocol.sef.util;

import java.util.HashSet;
import java.util.Set;

/**
 * @author gkountak
 *
 */
public class ConfigLoader {

    private static final ConfigLoader instance = new ConfigLoader();

    public static ConfigLoader getConfigLoader() {
        return instance;
    }

    private ConfigLoader() {
    }

    public Set<String> getReportedRegistries() {
        return Set.of("GB");
    }

    public boolean isInAggregatedMode() {
        return getAggregatedGroup() != null;
    }

    public String getAggregatedGroup() {
        return null;
    }

    public Integer getDbQueryFetchSize() {
        return null;
    }

    public Set<String> getDiscrepantResponseCodes() {
        return getSetOfParsedEntries();
    }


    private Set<String> getSetOfParsedEntries() {
        Set<String> resultSet = new HashSet<String>();
        String values = "4003,4004,4010,4011,4012,4014,4015,5008,5009,5018,5053,5054,5056,5061,5101,5102,5103,5104,5106,5156,5209,5211,5212,5213,5214,5215,5220,5255,5256,5301,5304,5305,5306,5307,5312,5313";
        String[] subPropertyArray = values.split(",");
        for (String entry : subPropertyArray) {
            resultSet.add(entry.trim().toUpperCase());
        }
        return resultSet;
    }
}
