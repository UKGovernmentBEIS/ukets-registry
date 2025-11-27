package gov.uk.ets.reports.generator.export.util;

import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.messaging.ReportGenerationCommand;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author Andreas Karmenis
 * @created 24/02/2023 - 2:50 PM
 * @project uk-ets-app-reports-generator
 */
@Log4j2
public class ComplianceReportTypeHelper {

    public static final String VERIFIED_EMISSIONS = "verified emissions";
    public static final String STATIC_SURRENDER_20 = "Static surrender status 20";
    private static final Pattern PATTERN = Pattern.compile("-?[1-9]\\d*|0");

    public static final Set<String> IN_10_YEARS_VALUES = Set.of (
        "UK Regulator",
        "AH name",
        "Account name",
        "Account type",
        "Installation name",
        "Installation ID / Aircraft operator ID",
        "Activity type",
        "Permit ID/Monitoring plan ID"
    );

    private static final Predicate<String> VERIFIED_BIAS = str -> str.toLowerCase().contains(VERIFIED_EMISSIONS);
    private static final Predicate<String> SURRENDER_BIAS = str ->
            !str.startsWith(STATIC_SURRENDER_20) && !IN_10_YEARS_VALUES.contains(str);
    private static final Set<ReportType> complianceTypes = Set.of (
            ReportType.R0032, // Compliance verified emissions and surrenders
            ReportType.R0033, // Compliance verified emissions
            ReportType.R0025  // Compliance 10 years
    );

    private static List<Integer> getColumnIndexes(List<String> reportHeaders, Predicate<String> p){
        if(CollectionUtils.isNotEmpty(reportHeaders)){
            return reportHeaders
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(p)
                    .map(reportHeaders::indexOf)
                    .toList();
        }
        return List.of();
    }

    public static boolean isOfComplianceTypeReport(ReportGenerationCommand command){
        return complianceTypes.contains(command.getType()) ;
    }

    public static Number convertToNumber(Object cellValue){
       return Long.parseLong(((String) cellValue));
    }

    public static boolean isNumeric(String cellValue){
        return PATTERN.matcher(cellValue).matches();
    }

    public static List<Integer> reportTypeChoice(ReportGenerationCommand command, List<String> reportHeaders){
        return (isOfComplianceTypeReport(command))
         ? switch (command.getType()) {
            case R0032, R0033 -> getColumnIndexes(reportHeaders, VERIFIED_BIAS);
            case R0025 -> getColumnIndexes(reportHeaders, SURRENDER_BIAS);
            default -> List.of();
           }
         : List.of();
    }
}
