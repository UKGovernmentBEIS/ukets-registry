package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.AddOrReplaceARTasksReportData;
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
public class AddOrReplaceARTasksService implements ReportTypeService<AddOrReplaceARTasksReportData> {

    private final ReportDataMapper<AddOrReplaceARTasksReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0018;
    }

    @Override
    public List<Object> getReportDataRow(AddOrReplaceARTasksReportData reportData) {
        List<Object> data = new ArrayList<>();

        data.add(reportData.getTaskRequestIdentifier());
        data.add(reportData.getRequestType());
        data.add(reportData.getInitiatedDate());
        data.add(reportData.getTaskWeeks());
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getAccountType());
        data.add(reportData.getAccountFullIdentifier());
        data.add(reportData.getDisclosedName());
        data.add(reportData.getUrid());
        data.add(reportData.getArUserStatus());
        data.add(reportData.getNameClaimant());
        data.add(reportData.getTotalOpenDocRequests());
        data.add(reportData.getTotalCompletedDocRequests());

        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of(
                "Task",
                "Task type",
                "Initiated (UTC)",
                "Task weeks",
                "AH name",
                "Account type",
                "Account number",
                "AR",
                "URID",
                "Status",
                "Name of claimant",
                "Open Doc Requests",
                "Completed Doc Requests"
            );
    }

    @Override
    public List<AddOrReplaceARTasksReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
