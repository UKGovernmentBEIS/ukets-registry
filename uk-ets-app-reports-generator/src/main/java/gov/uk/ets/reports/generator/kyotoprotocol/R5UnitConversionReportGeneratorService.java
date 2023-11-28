package gov.uk.ets.reports.generator.kyotoprotocol;

import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.NonSEFReportRunner;
import gov.uk.ets.reports.generator.kyotoprotocol.nonsef.protocol.NonSEFReport;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ReportTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.ConfigLoader;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.DateUtil;
import gov.uk.ets.reports.model.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class R5UnitConversionReportGeneratorService implements KyotoProtocolReportTypeService {

    private final DownloadFile downloadFile;

    @Override
    public ReportType reportType() {
        return ReportType.R0024;
    }

    @Override
    public void generateReportData(KyotoProtocolParams params) {
        ConfigLoader cl = ConfigLoader.getConfigLoader();
        NonSEFReport nonSefReportRunner = new NonSEFReportRunner(ReportTypeEnum.R5REG.name());
        KyotoReportOutcome fileReport = nonSefReportRunner.createReport(params.getReportedYear(),
                cl.getReportedRegistries(), cl.getDiscrepantResponseCodes(), params);
        downloadFile.getFile(fileReport, params);
    }
}