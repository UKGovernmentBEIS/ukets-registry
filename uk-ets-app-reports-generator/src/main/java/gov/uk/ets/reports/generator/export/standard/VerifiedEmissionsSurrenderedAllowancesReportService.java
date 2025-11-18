package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.VerifiedEmissionsSurrenderedAllowancesReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VerifiedEmissionsSurrenderedAllowancesReportService
    implements ReportTypeService<VerifiedEmissionsSurrenderedAllowancesReportData> {

    private final ReportDataMapper<VerifiedEmissionsSurrenderedAllowancesReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0019;
    }

    @Override
    public List<Object> getReportDataRow(VerifiedEmissionsSurrenderedAllowancesReportData reportData) {
        List<Object> data = new ArrayList<>();

        data.add(reportData.getRegulator());
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getInstallationName());
        data.add(reportData.getOperatorId());
        data.add(reportData.getPermitIdentifier());
        data.add(reportData.getMainActivityTypeCode());
        data.add(reportData.getYear());
        data.add(reportData.getAllocationEntitlement());
        data.add(reportData.getAllocationReturned());
        data.add(reportData.getAllocationReversed());
        data.add(reportData.getAllocationTotal());
        data.add(reportData.getVerifiedEmissions());
        data.add(reportData.getAccountClosure());
        data.add(reportData.getTotalSurrenderedAllowances());
        data.add(reportData.getDynamicComplianceStatus());
        data.add(reportData.getStaticComplianceStatus());

        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of(
                "Regulator",
                "AH Name",
                "Installation Name",
                "Operator ID",
                "Permit ID or Monitoring Plan ID",
                "Regulated activity",
                "Year",
                "Entitlement",
                "Returned Allowances",
                "Reversed Allowances",
                "Allowances Delivered",
                "Verified emissions",
                "Account Closed",
                "Surrendered Allowances",
                "DSS",
                "SSS"
            );
    }

    @Override
    public List<VerifiedEmissionsSurrenderedAllowancesReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
