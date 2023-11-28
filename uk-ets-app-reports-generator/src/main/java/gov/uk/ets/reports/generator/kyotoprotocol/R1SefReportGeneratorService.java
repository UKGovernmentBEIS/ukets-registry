package gov.uk.ets.reports.generator.kyotoprotocol;

import gov.uk.ets.reports.generator.kyotoprotocol.sef.AbstractSefReportCreator;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.SefReportCreatorFactory;
import gov.uk.ets.reports.model.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class R1SefReportGeneratorService implements KyotoProtocolReportTypeService {

    private final DownloadFile downloadFile;

    @Override
    public ReportType reportType() {
        return ReportType.R0020;
    }

    @Override
    public void generateReportData(KyotoProtocolParams params) {
        AbstractSefReportCreator sefCreator = SefReportCreatorFactory.getReportCreator(params);
        KyotoReportOutcome[] sefReports = sefCreator.createSefReports();
        for (KyotoReportOutcome fileReport: sefReports) {
            downloadFile.getFile(fileReport, params);
        }
    }
}
