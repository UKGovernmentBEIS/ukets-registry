package gov.uk.ets.compliance.domain;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReportingPeriod {

    int startYear;
    int endYear;

    public boolean isYearIn(int year) {
        return year >= startYear && year <= endYear;
    }


    public ReportingPeriod(int startYear, int endYear) {
        this.startYear = startYear;
        this.endYear = endYear;
    }

    public boolean startsInThePresentOrFutureOf(int currentYear) {
        return startYear >= currentYear;
    }

    public Set<Integer> getReportingYearsUntil(int year) {
        var end = Math.min(year, endYear);
        return IntStream.rangeClosed(startYear, end).boxed().collect(Collectors.toSet());
    }
}
