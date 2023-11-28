package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.AllocationPreparationReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AllocationPreparationReportService implements ReportTypeService<AllocationPreparationReportData> {

    private final ReportDataMapper<AllocationPreparationReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0030;
    }

    @Override
    public List<Object> getReportDataRow(AllocationPreparationReportData reportData) {
        List<Object> data = new ArrayList<>();

        data.add(reportData.getRegulator());
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getAccountNumber());
        data.add(reportData.getAccountName());
        data.add(reportData.getAccountType());
        data.add(reportData.getAccountStatus());
        data.add(reportData.getInstallationOrAircraftOperatorId());
        data.add(reportData.getPermitOrMonitoringPlanId());
        data.add(reportData.getInstallationName());
        data.add(reportData.getYear());
        data.add(reportData.getAllocationType());
        data.add(reportData.getEntitlement());
        data.add(reportData.getAllocated());
        data.add(reportData.getToBeReturned());
        data.add(reportData.getToBeDelivered());
        data.add(reportData.getExcluded());
        data.add(reportData.getWithheld());

        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of("Regulator",
                "Account Holder name",
                "Account number",
                "Account name",
                "Account type",
                "Account status",
                "Installation ID or Aircraft Operator ID",
                "Permit ID or Monitoring Plan ID",
                "Installation name",
                "Year",
                "Allocation table",
                "Entitlement",
                "Allowances delivered",
                "To be returned",
                "To be delivered",
                "Excluded",
                "Withheld");
    }

    @Override
    public List<AllocationPreparationReportData> generateReportData(ReportCriteria criteria) {
        return mapper.mapData(criteria);
    }

    @Override
    public List<AllocationPreparationReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
