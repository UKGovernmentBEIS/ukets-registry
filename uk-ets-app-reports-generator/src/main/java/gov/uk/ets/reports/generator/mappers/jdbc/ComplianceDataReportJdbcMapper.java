package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.ComplianceDataReportData;
import gov.uk.ets.reports.generator.export.util.DateRangeUtil;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ComplianceDataReportJdbcMapper
    implements ReportDataMapper<ComplianceDataReportData>, RowMapper<ComplianceDataReportData> {

    private final JdbcTemplate jdbcTemplate;

    /**
     * <p> The query returns information on compliant accounts for the years 2021 to 2030.</p>
     *
     * <p> Table t contains the report data per account and per year.</p>
     *
     */
    private static final String REPORT_QUERY = """
            SELECT date,
            oha_dss_error,
            oha_dss_not_applicable,
            oha_dss_exempt,
            oha_dss_c,
            oha_dss_b,
            oha_dss_a,
            total_oha,
            aoha_dss_error,
            aoha_dss_not_applicable,
            aoha_dss_exempt,
            aoha_dss_c,
            aoha_dss_b,
            aoha_dss_a,
            total_aoha,
            moha_dss_error,
            moha_dss_not_applicable,
            moha_dss_exempt,
            moha_dss_c,
            moha_dss_b,
            moha_dss_a,
            total_moha,
            oha_cumulative_emissions,
            oha_cumulative_surrenders - oha_cumulative_reversals as oha_cumulative_surrendered_allowances,
            oha_cumulative_surrenders - oha_cumulative_reversals - oha_cumulative_emissions as oha_surrender_balance,
            aoha_cumulative_emissions,
            aoha_cumulative_surrenders - aoha_cumulative_reversals as aoha_cumulative_surrendered_allowances,
            aoha_cumulative_surrenders - aoha_cumulative_reversals - aoha_cumulative_emissions as aoha_surrender_balance,
            moha_cumulative_emissions,
            moha_cumulative_surrenders - moha_cumulative_reversals as moha_cumulative_surrendered_allowances,
            moha_cumulative_surrenders - moha_cumulative_reversals - moha_cumulative_emissions as moha_surrender_balance,
            total_cumulative_emissions,
            total_cumulative_surrenders - total_cumulative_reversals as total_cumulative_surrendered_allowances,
            total_cumulative_surrenders - total_cumulative_reversals - total_cumulative_emissions as total_surrender_balance,
            oha_exempt_most_recent_applicable,
            oha_live_most_recent_applicable,
            aoha_exempt_most_recent_applicable,
            aoha_live_most_recent_applicable,
            moha_exempt_most_recent_applicable,
            moha_live_most_recent_applicable
               FROM compliance_records_summary
               WHERE date >= ?
               ORDER BY date DESC;
            """;

    @Override
    public List<ComplianceDataReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        int currentYear = LocalDateTime.now().getYear();
        LocalDateTime cutOffDateDate = LocalDateTime.parse(currentYear + "-01-01T00:00:00");
        return jdbcTemplate.query(REPORT_QUERY, this, cutOffDateDate.toLocalDate());
    }

    @Override
    public ComplianceDataReportData mapRow(ResultSet rs, int i) throws SQLException {
        return ComplianceDataReportData.builder()
            .activeDate(rs.getDate("date"))
                .oHADssError(rs.getLong("oha_dss_error"))
                .oHADssNotApplicable(rs.getLong("oha_dss_not_applicable"))
                .oHADssExempt(rs.getLong("oha_dss_exempt"))
                .oHADssC(rs.getLong("oha_dss_c"))
                .oHADssB(rs.getLong("oha_dss_b"))
                .oHADssA(rs.getLong("oha_dss_a"))
                .totalOHA(rs.getLong("total_oha"))
                .aOHADssError(rs.getLong("aoha_dss_error"))
                .aOHADssNotApplicable(rs.getLong("aoha_dss_not_applicable"))
                .aOHADssExempt(rs.getLong("aoha_dss_exempt"))
                .aOHADssC(rs.getLong("aoha_dss_c"))
                .aOHADssB(rs.getLong("aoha_dss_b"))
                .aOHADssA(rs.getLong("aoha_dss_a"))
                .totalAOHA(rs.getLong("total_aoha"))
                .mOHADssError(rs.getLong("moha_dss_error"))
                .mOHADssNotApplicable(rs.getLong("moha_dss_not_applicable"))
                .mOHADssExempt(rs.getLong("moha_dss_exempt"))
                .mOHADssC(rs.getLong("moha_dss_c"))
                .mOHADssB(rs.getLong("moha_dss_b"))
                .mOHADssA(rs.getLong("moha_dss_a"))
                .totalMOHA(rs.getLong("total_moha"))
                .oHACumulativeEmissions(rs.getBigDecimal("oha_cumulative_emissions"))
                .oHACumulativeSurrenderedAllowances(rs.getBigDecimal("oha_cumulative_surrendered_allowances"))
                .oHASurrenderBalance(rs.getBigDecimal("oha_surrender_balance"))
                .aOHACumulativeEmissions(rs.getBigDecimal("aoha_cumulative_emissions"))
                .aOHACumulativeSurrenderedAllowances(rs.getBigDecimal("aoha_cumulative_surrendered_allowances"))
                .aOHASurrenderBalance(rs.getBigDecimal("aoha_surrender_balance"))
                .mOHACumulativeEmissions(rs.getBigDecimal("moha_cumulative_emissions"))
                .mOHACumulativeSurrenderedAllowances(rs.getBigDecimal("moha_cumulative_surrendered_allowances"))
                .mOHASurrenderBalance(rs.getBigDecimal("moha_surrender_balance"))
                .totalCumulativeEmissions(rs.getBigDecimal("total_cumulative_emissions"))
                .totalCumulativeSurrenderedAllowances(rs.getBigDecimal("total_cumulative_surrendered_allowances"))
                .totalSurrenderBalance(rs.getBigDecimal("total_surrender_balance"))
                .ohaExemptMostRecentApplicable(rs.getLong("oha_exempt_most_recent_applicable"))
                .aOhaExemptMostRecentApplicable(rs.getLong("aoha_exempt_most_recent_applicable"))
                .mOhaExemptMostRecentApplicable(rs.getLong("moha_exempt_most_recent_applicable"))
                .ohaLiveMostRecentApplicable(rs.getLong("oha_live_most_recent_applicable"))
                .aOhaLiveMostRecentApplicable(rs.getLong("aoha_live_most_recent_applicable"))
                .mOhaLiveMostRecentApplicable(rs.getLong("moha_live_most_recent_applicable"))
                .build();
    }
}
