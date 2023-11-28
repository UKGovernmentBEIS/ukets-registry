package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.ARPerAccountReportData;
import gov.uk.ets.reports.generator.domain.Account;
import gov.uk.ets.reports.generator.domain.AccountAccess;
import gov.uk.ets.reports.generator.domain.AccountHolder;
import gov.uk.ets.reports.generator.domain.User;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ARPerAccountJdbcMapper
    implements ReportDataMapper<ARPerAccountReportData>, RowMapper<ARPerAccountReportData> {

    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY =
        "select ah.identifier,\n" +
            "       coalesce(ah.name, concat_ws(' ', ah.first_name, ah.last_name)) as holder_name,\n" +
            "       a.full_identifier,\n" +
            "       a.account_name,\n" +
            "       a.account_status,\n" +
            "       a.type_label,\n" +
            "       u.id,\n" +
            "       u.urid,\n" +
            "       u.iam_identifier,\n" +
            "       u.first_name,\n" +
            "       u.last_name,\n" +
            "       u.email,\n" +
            "       aa.state,\n" +
            "       aa.access_right\n" +
            "from account_holder ah,\n" +
            "     account a,\n" +
            "     account_access aa,\n" +
            "     users u\n" +
            "where a.account_holder_id = ah.id\n" +
            "  and aa.account_id = a.id\n" +
            "  and aa.user_id = u.id\n" +
            "  and aa.state in ('SUSPENDED', 'ACTIVE')\n" +
            "  and aa.access_right <> 'ROLE_BASED'\n" +
            "  and a.request_status != 'REQUESTED'\n" +
            "order by holder_name, a.full_identifier;";

    @Override
    public List<ARPerAccountReportData> mapData(ReportCriteria criteria) {
        return
            jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public List<ARPerAccountReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return
            jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public ARPerAccountReportData mapRow(ResultSet resultSet, int i) throws SQLException {
        return
            ARPerAccountReportData.builder()
                .accountHolder(AccountHolder.builder()
                    .id(resultSet.getLong(1))
                    .name(resultSet.getString(2))
                    .build())
                .account(Account.builder()
                    .number(resultSet.getString(3))
                    .name(resultSet.getString(4))
                    .status(resultSet.getString(5))
                    .type(resultSet.getString(6))
                    .build())
                .user(User.builder()
                    .id(resultSet.getString(7))
                    .urid(resultSet.getString(8))
                    .iamIdentifier(resultSet.getString(9))
                        .firstName(resultSet.getString(10))
                        .lastName(resultSet.getString(11))
                        .email(resultSet.getString(12))
                    .build())
                .accountAccess(AccountAccess.builder()
                    .state(resultSet.getString(13))
                    .accessRights(resultSet.getString(14))
                    .build())
                .build();
    }
}
