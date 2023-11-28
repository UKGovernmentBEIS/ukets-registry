package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.OrphanUserData;
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

@Service
@RequiredArgsConstructor
public class OrphanUsersReportService implements ReportTypeService<OrphanUserData> {

    private final ReportDataMapper<OrphanUserData> mapper;

    @Override
    public List<OrphanUserData> generateReportData(ReportCriteria reportCriteria) {
        return mapper.mapData(reportCriteria);
    }

    @Override
    public List<OrphanUserData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }

    @Override
    public List<Object> getReportDataRow(OrphanUserData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getUser().getUrid());
        return data;
    }


    @Override
    public List<String> getReportHeaders(Long year) {
        return Stream
            .of("User URID").collect(
                Collectors.toList());
    }

    @Override
    public ReportType reportType() {
        return ReportType.R0002;
    }
}
