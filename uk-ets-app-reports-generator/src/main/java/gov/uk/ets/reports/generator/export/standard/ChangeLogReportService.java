package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.ChangeLogReportData;
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
public class ChangeLogReportService implements ReportTypeService<ChangeLogReportData> {

    private final ReportDataMapper<ChangeLogReportData> mapper;

    @Override
    public List<ChangeLogReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }

    @Override
    public List<Object> getReportDataRow(ChangeLogReportData reportData) {
        List<Object> data = new ArrayList<>();

        data.add(reportData.getFieldChanged());
        data.add(reportData.getOldValue());
        data.add(reportData.getNewValue());
        data.add(reportData.getEntity());
        data.add(reportData.getAccountNumber());
        data.add(reportData.getOperatorId());
        data.add(reportData.getUpdatedBy());
        data.add(reportData.getUpdatedAt());

        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List.of("Field Changed",
                       "Old Value",
                       "New Value",
                       "Entity",
                       "Account Number",
                       "Operator ID",
                       "Updated by",
                       "Updated at");
    }

    @Override
    public ReportType reportType() {
        return ReportType.R0051;
    }
}
