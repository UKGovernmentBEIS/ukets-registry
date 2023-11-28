package gov.uk.ets.reports.generator.config;

import gov.uk.ets.reports.generator.domain.ReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.kyotoprotocol.KyotoProtocolReportTypeService;
import gov.uk.ets.reports.model.ReportType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportsOutputConfig {

    @Bean
    public Map<ReportType, ReportTypeService<ReportData>> reportTypeServiceMap(
        List<ReportTypeService> reportTypeServiceList) {
        return reportTypeServiceList.stream().collect(
            Collectors.toMap(ReportTypeService::reportType, reportTypeService -> reportTypeService));
    }

    @Bean
    public Map<ReportType, KyotoProtocolReportTypeService> reportTypeKyotoProtocolServiceMap(
            List<KyotoProtocolReportTypeService> reportTypeServiceList) {
        return reportTypeServiceList.stream().collect(
                Collectors.toMap(KyotoProtocolReportTypeService::reportType, reportTypeService -> reportTypeService));
    }

}
