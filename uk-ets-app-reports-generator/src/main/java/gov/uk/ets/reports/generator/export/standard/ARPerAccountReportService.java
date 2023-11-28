package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.ARPerAccountReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ARPerAccountReportService
    implements ReportTypeService<ARPerAccountReportData> {

    private final ReportDataMapper<ARPerAccountReportData> mapper;

    public List<ARPerAccountReportData> generateReportData(ReportCriteria reportCriteria) {
        return mapper.mapData(reportCriteria);
    }

    @Override
    public List<ARPerAccountReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }

    @Override
    public List<Object> getReportDataRow(ARPerAccountReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getAccountHolder().getId());
        data.add(reportData.getAccountHolder().getName());
        data.add(reportData.getAccount().getNumber());
        data.add(reportData.getAccount().getName());
        data.add(reportData.getAccount().getStatus());
        data.add(reportData.getAccount().getType());
        data.add(reportData.getUser().getUrid());
        data.add(reportData.getUser().getFirstName());
        data.add(reportData.getUser().getLastName());
        data.add(reportData.getUser().getEmail());
        data.add(reportData.getAccountAccess().getState());
        data.add(reportData.getAccountAccess().getAccessRights());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return Stream
            .of("AH ID", "AH Name", "Account number", "Account name", "Account status",
                "Account type", "User URID", "First name", "Last name", "Email", "AR status",
                "Access rights").collect(Collectors.toList());
    }

    @Override
    public ReportType reportType() {
        return ReportType.R0003;
    }
}
