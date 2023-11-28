package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.protocol;

import gov.uk.ets.reports.generator.kyotoprotocol.KyotoProtocolParams;
import gov.uk.ets.reports.generator.kyotoprotocol.KyotoReportOutcome;

import java.util.Set;

/**
 * Used to define the interface for executing a non SEF report.
 * @author gkountak
 */
public interface NonSEFReport {

    /**
     * Create a report for the parameters.
     *
     * @param reportedYear
     * @param parties
     * @param discrResponseCodes
     */
    KyotoReportOutcome createReport(short reportedYear, Set<String> parties, Set<String> discrResponseCodes,
                      KyotoProtocolParams params);
}
