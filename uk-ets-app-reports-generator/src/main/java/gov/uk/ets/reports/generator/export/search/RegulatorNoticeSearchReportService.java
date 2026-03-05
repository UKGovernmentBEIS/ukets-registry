package gov.uk.ets.reports.generator.export.search;

import gov.uk.ets.reports.generator.domain.CompliantEntity;
import gov.uk.ets.reports.generator.domain.RegulatorNoticeSearchReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RegulatorNoticeSearchReportService implements ReportTypeService<RegulatorNoticeSearchReportData> {

    private final ReportDataMapper<RegulatorNoticeSearchReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0052;
    }

    @Override
    public List<Object> getReportDataRow(RegulatorNoticeSearchReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getRegulatorNotice().getTaskId());
        data.add(reportData.getAccountHolder().getName());
        CompliantEntity compliantEntity = reportData.getCompliantEntity();
        data.add(ObjectUtils.firstNonNull(
                compliantEntity.getInstallationPermitId(),
                compliantEntity.getAircraftMonitoringPlanId(),
                compliantEntity.getMaritimeMonitoringPlanId()));
        data.add(reportData.getRegulatorNotice().getProcessType());
        data.add(reportData.getRegulatorNotice().getCreatedOn());
        data.add(reportData.getRegulatorNotice().getCompletedOn() != null ? reportData.getRegulatorNotice().getCompletedOn() :
                reportData.getRegulatorNotice().getClaimedOn());
        data.add(reportData.getRegulatorNotice().getClaimantName());
        data.add(reportData.getRegulatorNotice().getTaskStatus());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
                .of("Notice ID",
                        "Account Holder name",
                        "Permit/EMP ID",
                        "Notice type",
                        "Created on (UTC)",
                        "Updated on (UTC)",
                        "Name of claimant",
                        "Task status");
    }

    @Override
    public List<RegulatorNoticeSearchReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
