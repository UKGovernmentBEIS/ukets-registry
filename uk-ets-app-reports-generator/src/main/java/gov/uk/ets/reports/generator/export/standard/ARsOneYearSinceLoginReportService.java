package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.ARsOneYearSinceLoginReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ARsOneYearSinceLoginReportService implements ReportTypeService<ARsOneYearSinceLoginReportData> {

    private final ReportDataMapper<ARsOneYearSinceLoginReportData> mapper;

    @Override
    public List<ARsOneYearSinceLoginReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }

    @Override
    public List<Object> getReportDataRow(ARsOneYearSinceLoginReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getAccountId());
        data.add(reportData.getAccountType());
        data.add(reportData.getAccountStatus());
        data.add(reportData.getFirstReportingYear());
        data.add(reportData.getLastReportingYear());
        data.add(reportData.getSurrenderStatus());
        data.add(reportData.getUserUrid());
        data.add(reportData.getUserFirstName());
        data.add(reportData.getUserLastName());
        data.add(reportData.getUserStatus());
        data.add(reportData.getUserEmail());
        data.add(reportData.getWeeksSinceRegistered());
        data.add(reportData.getWeeksSinceLogin());
        data.add(reportData.getPrimaryContactFirstName());
        data.add(reportData.getPrimaryContactLastName());
        data.add(reportData.getPrimaryContactEmail());
        data.add(reportData.getAlternativeContactFirstName());
        data.add(reportData.getAlternativeContactLastName());
        data.add(reportData.getAlternativeContactEmail());
        data.add(null); //to be filled by user
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List.of(
            "AH Name",
            "Account number",
            "Account type",
            "Account status",
            "FYV",
            "LYV",
            "Surrender Status",
            "URID",
            "User First Name",
            "User Last Name",
            "User Status",
            "User Email Address",
            "Weeks since registered",
            "Weeks since login",
            "PC First Name",
            "PC Last Name",
            "PC Email Address",
            "APC First Name",
            "APC Last Name",
            "APC Email Address",
            "Don't chase"
        );
    }

    @Override
    public ReportType reportType() {
        return ReportType.R0047;
    }
}
