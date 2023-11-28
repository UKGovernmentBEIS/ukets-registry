package gov.uk.ets.compliance.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Holds information related to the yearly emissions
 * More particular
 * a reportedEmissionsMap which holds all years for which emissions have been reported
 * an excludedYearsSet which holds all the years for which emissions should be excluded.
 */
@NoArgsConstructor
@Getter
public class YearlyEmissions {

    //Key : year Value: emissions
    private Map<Integer, Long> reportedEmissionsMap = new HashMap<>();

    private Set<Integer> excludedYearsSet = new HashSet<>();

    public void updateEmissions(Integer year, Long amount) {
        reportedEmissionsMap.put(year, amount);
    }

    public void resetEmissionsForYear(Integer year) {
        reportedEmissionsMap.remove(year);
    }

    public Long getEmissionsForYear(Integer year) {
        return reportedEmissionsMap.get(year);
    }
    
    public Long countEmissions() {
        return reportedEmissionsMap.entrySet()
            .stream()
            .map(Entry::getValue)
            .mapToLong(Long::valueOf)
            .sum();
    }
    
    public Long countEmissionsExceptYear(int year) {
        return reportedEmissionsMap.entrySet()
            .stream()
            .filter(e -> e.getKey() != year)
            .map(Entry::getValue)
            .mapToLong(Long::valueOf)
            .sum();
    }

    /**
     * Calculate the years with missing emissions.
     *
     * @param from the start year that emissions are required
     * @param to   the end year that emissions are required
     * @return a list of years where emissions are not reported.
     */
    public List<Integer> getYearsWithMissingEmissions(Integer from, Integer to) {
        List<Integer> yearsWithMissingEmissions = new ArrayList<>();
        IntStream.rangeClosed(from, to).forEach(requiredYear -> {
            if (!reportedEmissionsMap.containsKey(requiredYear)) {
                yearsWithMissingEmissions.add(requiredYear);
            }
        });
        return yearsWithMissingEmissions;
    }

    public boolean hasNonZeroEmissionsForYear(Integer year) {
        return getEmissionsForYear(year) != null && getEmissionsForYear(year) > 0;
    }
    
    public boolean hasZeroEmissionsForYear(Integer year) {
        return getEmissionsForYear(year) != null && getEmissionsForYear(year) == 0;
    }
}
