package gov.uk.ets.reports.generator.kyotoprotocol.nonsef;

import gov.uk.ets.reports.generator.kyotoprotocol.KyotoProtocolParams;
import gov.uk.ets.reports.generator.kyotoprotocol.KyotoReportOutcome;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.protocol.NonSEFReport;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r2itl.RREG2Report;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r3itl.RREG3Report;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r4itl.RREG4Report;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r5itl.REG5Report;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ReportTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Runs the creation of a non sef report by selecting the correct implementation.
 * @author gkountak
 */
@Getter
@Setter
public class NonSEFReportRunner implements NonSEFReport {

    private String reportType;
    private NonSEFReport report;
    private Set<String> responseCodes;

    public NonSEFReportRunner(String reportType) {
        super();
        this.reportType = reportType;
        this.responseCodes = new HashSet<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KyotoReportOutcome createReport(short reportedYear, Set<String> parties, Set<String> discrResponseCodes,
                             KyotoProtocolParams params) {

        updateReportType(discrResponseCodes);

        return report.createReport(reportedYear, parties, responseCodes, params);
    }

    public void updateReportType(Set<String> discrResponseCodes) {
        responseCodes.addAll(discrResponseCodes);
        if (ReportTypeEnum.R2REG.name().equals(reportType)) {
            report = new RREG2Report();
        }  else if (ReportTypeEnum.R3REG.name().equals(reportType)) {
            report = new RREG3Report();
        }  else if (ReportTypeEnum.R4REG.name().equals(reportType)) {
            report = new RREG4Report();
        } else if (ReportTypeEnum.R5REG.name().equals(reportType)) {
            report = new REG5Report();
            responseCodes = null;
        } else {
            throw new IllegalStateException("Report type " + this.reportType + " value is invalid");
        }
    }

}
