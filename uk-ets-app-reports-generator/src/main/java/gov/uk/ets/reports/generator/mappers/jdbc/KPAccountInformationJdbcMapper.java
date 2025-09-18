package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.KPAccountInformationReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class KPAccountInformationJdbcMapper
    implements ReportDataMapper<KPAccountInformationReportData>, RowMapper<KPAccountInformationReportData> {

    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY =
            "select a.account_name                                         as account_name,\n" +
            "       'Confidential'                                         as account_number,\n" +
            "       a.kyoto_account_type                                   as account_type,\n" +
            "       a.commitment_period_code                               as commitment_period_code,\n" +
            "       case\n" +
            "           when ah.type in ('ORGANISATION','GOVERNMENT','INDIVIDUAL') then ah.name\n" +
            "           else CONCAT(ahr.first_name,' ', ahr.last_name) end as account_holder_name,\n" +
            "       count(aa.user_id)                                      as num_of_ars\n" +
            "from account a\n" +
            "         join account_holder ah\n" +
            "                   on a.account_holder_id = ah.id\n" +
            "         left join account_holder_representative ahr\n" +
            "                   on ahr.account_holder_id = ah.id and ahr.account_contact_type = 'PRIMARY'\n" +
            "         left join account_access aa\n" +
            "                   on aa.account_id = a.id\n" +
            "where a.registry_account_type = 'NONE'\n" +
            "  and a.account_status not in ('CLOSED', 'REJECTED')\n" +
            "group by a.account_name,\n" +
            "         a.full_identifier,\n" +
            "         a.kyoto_account_type,\n" +
            "         a.commitment_period_code,\n" +
            "         account_holder_name\n" +
            "order by a.full_identifier asc";

    @Override
    public List<KPAccountInformationReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public KPAccountInformationReportData mapRow(ResultSet rs, int i) throws SQLException {
        return KPAccountInformationReportData.builder()
            .accountName(rs.getString("account_name"))
            .accountNumber(rs.getString("account_number"))
            .accountType(rs.getString("account_type"))
            .commitmentPeriod(rs.getInt("commitment_period_code"))
            .accountHolderName(rs.getString("account_holder_name"))
            .numberOfARs(rs.getInt("num_of_ars"))
            .build();
    }
}
