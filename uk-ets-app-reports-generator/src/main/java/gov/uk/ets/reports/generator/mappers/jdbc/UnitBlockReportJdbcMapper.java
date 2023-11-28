package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.UnitBlockReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportRequestingRole;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class UnitBlockReportJdbcMapper
    implements ReportDataMapper<UnitBlockReportData>, RowMapper<UnitBlockReportData> {

    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY =
        "select ub.id                    as unit_block_id,\n" +
            "       ub.start_block              as start_block,\n" +
            "       ub.end_block                as end_block,\n" +
            "       ub.end_block - ub.start_block + 1  as quantity,\n" +
            "       ub.unit_type             as unit_type,\n" +
            "       ub.originating_country_code as originating_country_code,\n" +
            "       account_identifier       as account_identifier,\n" +
            "       case \n" +
            "           when a.registry_account_type != 'NONE' then a.registry_account_type \n" +
            "           else a.kyoto_account_type \n" +
            "           end as account_type,\n" +
            "       ub.original_period          as original_cp,\n" +
            "       ub.applicable_period        as applicable_cp,\n" +
            "       case \n" +
            "           when project_number is null then 'NOT APPLICABLE'\n" +
            "           else project_number\n" +
            "           end as project_number,\n" +
            "       case \n" +
            "           when project_track is null then 'NOT APPLICABLE'\n" +
            "           else project_track \n" +
            "           end as project_track,\n" +
            "       case\n" +
            "           when sop = TRUE then 'TRUE'\n" +
            "           when sop = FALSE then 'FALSE'\n" +
            "           end                  as sop,\n" +
            "       case \n" +
            "           when environmental_activity is null then 'NOT APPLICABLE'\t\n" +
            "           else environmental_activity \n" +
            "           end as environmental_activity,\n" +
            "       case \n" +
            "           when reserved_for_transaction is null then 'NOT RESERVED' \n" +
            "           else reserved_for_transaction\n" +
            "           end as reserved_for_transaction,\n" +
            "       case\n" +
            "           when replaced = TRUE then 'TRUE'\n" +
            "           when replaced = FALSE then 'FALSE'\n" +
            "           else ''\n" +
            "           end                  as replaced,\n" +
            "       case \n" +
            "           when reserved_for_replacement is null then 'NOT RESERVED'\n" +
            "           else reserved_for_replacement\n" +
            "           end as reserved_for_replacement,\n" +
            "       case when tr_finder.identifier is null then 'NOT APPLICABLE'  \n" +
            "       else tr_finder.identifier \n" +
            "       end as last_transaction_id \n" +
            "from unit_block ub\n" +
            "         inner join account a on ub.account_identifier = a.identifier\n" +
            "         left join ( " +
            "                     select tb.start_block, tb.end_block, max(tr.id) as id " +
            "                     from transaction_block tb, " +
            "                          transaction tr " +
            "                     where tb.transaction_id = tr.id " +
            "                     and tr.status = 'COMPLETED' " +
            "                     group by tb.start_block, tb.end_block " +
            "                   ) as tr_helper  " +
            "          on  tr_helper.start_block = ub.start_block and tr_helper.end_block = ub.end_block  " +
            "          left join transaction tr_finder on tr_finder.id = tr_helper.id " +
            "order by ub.id desc";

    @Override
    public List<UnitBlockReportData> mapData(ReportCriteria criteria) {
        return List.of();
    }

    @Override
    public List<UnitBlockReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        List<UnitBlockReportData> result = jdbcTemplate.query(REPORT_QUERY, this);

        Set<String> etsUnitBlockTypes =
            Set.of("NON_KYOTO", "MULTIPLE", "ALLOWANCE", "ALLOWANCE_CP0", "ALLOWANCE_CHAPTER3");

        return result.stream()
            .filter(r -> ReportRequestingRole.authority.equals(reportQueryInfo.getRequestingRole()) &&
                etsUnitBlockTypes.contains(r.getUnitType()) ||
                ReportRequestingRole.administrator.equals(reportQueryInfo.getRequestingRole()))
            .collect(Collectors.toList());
    }

    @Override
    public UnitBlockReportData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UnitBlockReportData.builder()
            .unitBlockId(rs.getLong("unit_block_id"))
            .startBlock(rs.getLong("start_block"))
            .endBlock(rs.getLong("end_block"))
            .quantity(rs.getLong("quantity"))
            .unitType(rs.getString("unit_type"))
            .originatingCountryCode(rs.getString("originating_country_code"))
            .accountNumber(rs.getLong("account_identifier"))
            .accountType(rs.getString("account_type"))
            .originalCp(rs.getString("original_cp"))
            .applicableCp(rs.getString("applicable_cp"))
            .projectNumber(rs.getString("project_number"))
            .projectTrack(rs.getString("project_track"))
            .lastTransactionId(rs.getString("last_transaction_id"))
            .sop(rs.getString("sop"))
            .environmentalActivity(rs.getString("environmental_activity"))
            .reservedForTransaction(rs.getString("reserved_for_transaction"))
            .replaced(rs.getString("replaced"))
            .reservedForReplacement(rs.getString("reserved_for_replacement"))
            .build();
    }
}
