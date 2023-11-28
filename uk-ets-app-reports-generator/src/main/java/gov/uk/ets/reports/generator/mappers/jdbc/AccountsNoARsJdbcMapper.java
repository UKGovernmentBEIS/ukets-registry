package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.Util;
import gov.uk.ets.reports.generator.domain.Account;
import gov.uk.ets.reports.generator.domain.AccountHolder;
import gov.uk.ets.reports.generator.domain.AccountsNoARsReportData;
import gov.uk.ets.reports.generator.domain.CompliantEntity;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AccountsNoARsJdbcMapper
    implements ReportDataMapper<AccountsNoARsReportData>, RowMapper<AccountsNoARsReportData> {

    /**
     * <p> First 'not exists' clause filters out the accounts that already have ARs.</p>
     *
     * <p> Second 'not exists' clause filters out the accounts that have pending AR addition tasks.
     * Finally we filter out CLOSED or PROPOSED account statuses. </p>
     *
     * <p> Note that the account holder name is null in case of an individual (not always)
     * so we use their first and last name instead. </p>
     */
    public static final String REPORT_QUERY =
        "select ah.identifier                                                               as holder_id,\n" +
            "       coalesce(ah.name, concat_ws(' ', ah.first_name, ah.last_name))          as holder_name,\n" +
            "       a.full_identifier                                                       as account_number,\n" +
            "       a.account_name                                                          as account_name,\n" +
            "       a.account_status                                                        as account_status,\n" +
            "       a.type_label                                                            as account_type,\n" +
            "       a.opening_date                                                          as opening_date,\n" +
            "       case when i.compliant_entity_id IS NOT NULL then ce.identifier end      as installation_id,\n" +
            "       i.installation_name                                                     as installation_name,\n" +
            "       i.activity_type                                                         as installation_activity,\n" +
            "       i.permit_identifier                                                     as installation_permit_id,\n" +
            "       ce.regulator                                                            as regulator,\n" +
            "       ce.start_year                                                           as first_year_verified_emission_submission,\n" +
            "       ce.end_year                                                             as last_year_verified_emission_submission,\n" +
            "       a.compliance_status                                                     as compliance_status\n" +
            "from account a\n" +
            "         inner join account_holder ah on a.account_holder_id = ah.id\n" +
            "         left join compliant_entity ce on a.compliant_entity_id = ce.id\n" +
            "         left join installation i on ce.id = i.compliant_entity_id\n" +
            "where not exists(\n" +
            "        select aa.id\n" +
            "        from account_access aa\n" +
            "        where aa.account_id = a.id\n" +
            "          and aa.state in ('ACTIVE', 'REQUESTED', 'SUSPENDED')\n" +
            "          and aa.access_right <> 'ROLE_BASED'" +
            "    )\n" +
            "  and not exists(\n" +
            "        select t.id\n" +
            "        from task t\n" +
            "        where t.account_id = a.id\n" +
            "          and t.type = 'AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST'\n" +
            "          and t.status = 'SUBMITTED_NOT_YET_APPROVED'\n" +
            "    )\n" +
            "  and a.account_status not in ('CLOSED', 'PROPOSED', 'REJECTED')\n" +
            "order by holder_name, account_number";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<AccountsNoARsReportData> mapData(ReportCriteria criteria) {
        return jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public List<AccountsNoARsReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public AccountsNoARsReportData mapRow(ResultSet resultSet, int i) throws SQLException {
        return
            AccountsNoARsReportData.builder()
                .accountHolder(AccountHolder.builder()
                    .id(Util.getNullableLong(resultSet, "holder_id"))
                    .name(resultSet.getString("holder_name"))
                    .build())
                .account(Account.builder()
                    .number(resultSet.getString("account_number"))
                    .name(resultSet.getString("account_name"))
                    .status(resultSet.getString("account_status"))
                    .type(resultSet.getString("account_type"))
                    .openingDate(LocalDateTime.parse(resultSet.getString("opening_date"), formatter))
                    .complianceStatus(resultSet.getString("compliance_status"))
                    .build())
                .compliantEntity(CompliantEntity.builder()
                    .regulator(resultSet.getString("regulator"))
                    .installationId(Util.getNullableLong(resultSet, "installation_id"))
                    .installationName(resultSet.getString("installation_name"))
                    .installationActivity(resultSet.getString("installation_activity"))
                    .installationPermitId(resultSet.getString("installation_permit_id"))
                    .firstYearOfVerifiedEmissionSubmission(Util.getNullableLong(resultSet, "first_year_verified_emission_submission"))
                    .lastYearOfVerifiedEmissionSubmission(Util.getNullableLong(resultSet, "last_year_verified_emission_submission"))
                    .build())
                .build();
    }
}
