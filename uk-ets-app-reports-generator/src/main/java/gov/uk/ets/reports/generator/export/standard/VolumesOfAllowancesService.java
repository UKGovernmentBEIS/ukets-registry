package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.VolumeOfAllowancesReportData;
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
public class VolumesOfAllowancesService implements ReportTypeService<VolumeOfAllowancesReportData> {

    private final ReportDataMapper<VolumeOfAllowancesReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0015;
    }

    @Override
    public List<Object> getReportDataRow(VolumeOfAllowancesReportData reportData) {
        List<Object> data = new ArrayList<>();

        data.add(reportData.getAccountType());
        data.add(reportData.getNumberOfUnits());

        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of(
                "UKETS Account Types",
                "Number of Allowances "
            );
    }

    @Override
    public List<VolumeOfAllowancesReportData> generateReportData(ReportCriteria criteria) {
        return mapper.mapData(criteria);
    }

    @Override
    public List<VolumeOfAllowancesReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
