package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.KPAccountInformationReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class KPAccountInformationReportService implements ReportTypeService<KPAccountInformationReportData> {

    private final ReportDataMapper<KPAccountInformationReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0011;
    }

    @Override
    public List<Object> getReportDataRow(KPAccountInformationReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getAccountName());
        data.add(reportData.getAccountNumber());
        data.add(reportData.getAccountType());
        data.add(reportData.getCommitmentPeriod());
        data.add(reportData.getAccountHolderName());
        /*
         * According to business request, the excel file should include the info of the AR of each account but without showing the information.
         * We should only display the word confidential or empty if there isn't any. According to UKETS-5244 for all accounts we should show
         * the columns for two ARs and show the CONFIDENTIAL value for all the accounts regardless of the actual data of the accounts.
         */
        IntStream.range(0, 2).forEach(ar-> {
            data.add("Confidential");
            data.add("Confidential");
        });
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List.of("Account Name", "Account Number", "KP Account Type", "Commitment Period", "Account Holder",
                "1st Representative", "1st Rep Name and Contact",
                "2nd Representative", "2nd Rep Name and Contact"
        );
    }

    @Override
    public List<KPAccountInformationReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
