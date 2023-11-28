package gov.uk.ets.reports.generator.kyotoprotocol;

import gov.uk.ets.reports.generator.ReportGeneratorException;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.DateUtil;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.messaging.ReportGenerationCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KyotoProtocolReportGeneratorService {

    @Value("${spring.datasource.jdbc-url}")
    private String jdbcUrl;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    private final Map<ReportType, KyotoProtocolReportTypeService> reportTypeServiceMap;

    public void generateKyotoProtocolReport(ReportGenerationCommand command) {
        KyotoProtocolParams params = KyotoProtocolParams.builder()
                .reportGeneratorCommandId(command.getId())
                .reportedYear(command.getReportQueryInfo().getYear().shortValue())
                .commitmentPeriod(Optional.ofNullable(command.getReportQueryInfo().getCommitmentPeriod()))
                .requestingSystem(command.getRequestingSystem())
                .reportEndDate(null)
                .submissionYear((short)DateUtil.getYearFromDate(new Date()))
                .jdbcUrl(jdbcUrl)
                .username(username)
                .password(password)
                .build();

        // Choose the requested kyoto protocol report service
        KyotoProtocolReportTypeService reportTypeService = reportTypeServiceMap.get(command.getType());
        if (reportTypeService == null) {
            throw new ReportGeneratorException(String.format("Not supported report type for :%s", command.getType()));
        }

        reportTypeService.generateReportData(params);
    }

}
