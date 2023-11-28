package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.UnitBlockReportData;
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
public class UnitBlockReportService implements ReportTypeService<UnitBlockReportData> {
    private final ReportDataMapper<UnitBlockReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0029;
    }

    @Override
    public List<Object> getReportDataRow(UnitBlockReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getUnitBlockId());
        data.add(reportData.getStartBlock());
        data.add(reportData.getEndBlock());
        data.add(reportData.getQuantity());
        data.add(reportData.getUnitType());
        data.add(reportData.getOriginatingCountryCode());
        data.add(reportData.getAccountNumber());
        data.add(reportData.getAccountType());
        data.add(reportData.getOriginalCp());
        data.add(reportData.getApplicableCp());
        data.add(reportData.getProjectNumber());
        data.add(reportData.getProjectTrack());
        data.add(reportData.getLastTransactionId());
        data.add(reportData.getSop());
        data.add(reportData.getEnvironmentalActivity());
        data.add(reportData.getReservedForTransaction());
        data.add(reportData.getReplaced());
        data.add(reportData.getReservedForReplacement());

        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of("Unit block ID",
                "Start block",
                "End block",
                "Quantity",
                "Unit type",
                "Originating country code",
                "Holding account ID",
                "Holding account type",
                "Original CP",
                "Applicable CP",
                "Project number",
                "Project track",
                "Last transaction",
                "SOP",
                "Environmental activity",
                "Reserved for transaction",
                "Replaced",
                "Reserved for replacement");
    }

    @Override
    public List<UnitBlockReportData> generateReportData(ReportCriteria criteria) {
        return mapper.mapData(criteria);
    }

    @Override
    public List<UnitBlockReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
