package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.EntitiesAuthorisedToHoldUnitsReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EntitiesAuthorisedToHoldUnitsReportService implements ReportTypeService<EntitiesAuthorisedToHoldUnitsReportData> {

    private final ReportDataMapper<EntitiesAuthorisedToHoldUnitsReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0010;
    }

    @Override
    public List<Object> getReportDataRow(EntitiesAuthorisedToHoldUnitsReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getAuthorisedLegalEntity());
        data.add(reportData.getContactInformation());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List.of("Authorised Legal Entity", "Contact Information");
    }

    @Override
    public List<EntitiesAuthorisedToHoldUnitsReportData> generateReportData(ReportCriteria criteria) {
        return mapper.mapData(criteria);
    }

    @Override
    public List<EntitiesAuthorisedToHoldUnitsReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
