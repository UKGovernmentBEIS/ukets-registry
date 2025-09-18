package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.AccountHoldersNotesReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountHoldersNotesReportService implements ReportTypeService<AccountHoldersNotesReportData> {

    private final ReportDataMapper<AccountHoldersNotesReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0046;
    }

    @Override
    public List<Object> getReportDataRow(AccountHoldersNotesReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getAccountHolderId());
        data.add(reportData.getCreationDate());
        data.add(Optional.ofNullable(reportData.getUserKnownAs())
            .filter(knownAs -> !knownAs.isBlank())
            .orElse(reportData.getUserFirstName() + " " + reportData.getUserLastName()));
        data.add(reportData.getNote());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List.of(
            "AH Name",
            "AH ID",
            "Date and Time",
            "Created by",
            "Note"
        );
    }

    @Override
    public List<AccountHoldersNotesReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
