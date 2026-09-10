package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.ARPerAccountReportData;
import gov.uk.ets.reports.generator.domain.ServiceAgentsReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ServiceAgentsReportService implements ReportTypeService<ServiceAgentsReportData> {

    private final ReportDataMapper<ServiceAgentsReportData> mapper;

    @Override
    public List<ServiceAgentsReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }

    @Override
    public List<Object> getReportDataRow(ServiceAgentsReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getCompanyName());
        data.add(reportData.getEmail());
        data.add(reportData.getPhoneNumber());

        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return Stream
                .of("Company name", "Contact e-mail", "Contact number").collect(Collectors.toList());
    }

    @Override
    public ReportType reportType() {
        return ReportType.R0053;
    }
}
