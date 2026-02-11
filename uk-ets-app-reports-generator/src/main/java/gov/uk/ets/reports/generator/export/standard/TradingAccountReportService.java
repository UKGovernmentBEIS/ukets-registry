package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.TradingAccountReportData;
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
public class TradingAccountReportService implements ReportTypeService<TradingAccountReportData> {

    private final ReportDataMapper<TradingAccountReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0012;
    }

    @Override
    public List<Object> getReportDataRow(TradingAccountReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getSalesContactEmail());
        data.add(reportData.getSalesContactPhone());
        data.add(reportData.getUka1To99());
        data.add(reportData.getUka100To999());
        data.add(reportData.getUka1000Plus());
        data.add(reportData.getRegistrationNumber());
        data.add(reportData.getAddress());
        data.add(reportData.getCity());
        data.add(reportData.getStateOrProvince());
        data.add(reportData.getPostcode());
        data.add(reportData.getCountry());
        data.add(reportData.getAccountStatus());
        data.add(reportData.getOpenYear());
        data.add(reportData.getClose());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List.of("Account Holder", "UKA Sales Contact Email", "UKA Sales Contact Phone", "1-99 UKAs", "100-999 UKAs", "1000+ UKAs",
                "Registration Number", "Address", "Town or City", "State or Province", "Postal Code or ZIP",
                "Country", "Account status", "Open", "Closed");
    }

    @Override
    public List<TradingAccountReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
