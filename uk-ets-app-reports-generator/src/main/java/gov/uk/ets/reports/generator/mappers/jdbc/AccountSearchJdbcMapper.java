package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.Account;
import gov.uk.ets.reports.generator.domain.AccountHolder;
import gov.uk.ets.reports.generator.domain.AccountSearchReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;

import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Log4j2
public class AccountSearchJdbcMapper
    implements ReportDataMapper<AccountSearchReportData>, RowMapper<AccountSearchReportData> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<AccountSearchReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        Comparator<AccountSearchReportData> ahNameComparator = Comparator.comparing(a -> a.getAccountHolder().getName());
        Comparator<AccountSearchReportData> accountSearchReportDataComparator = ahNameComparator.thenComparing(a -> a.getAccount().getNumber());
        return jdbcTemplate.query(reportQueryInfo.getQuery(), this).stream()
            .sorted(accountSearchReportDataComparator)
            .collect(Collectors.toList());
    }

    @Override
    public AccountSearchReportData mapRow(ResultSet rs, int i) throws SQLException {

        return
            AccountSearchReportData.builder()
                .account(Account.builder()
                    .number(rs.getString(14))
                    .name(rs.getString(3))
                    .type(rs.getString(5))
                    .status(rs.getString(4))
                    .complianceStatus(rs.getString(11))
                    .balance(rs.getLong(7))
                    .regulatorGroup(rs.getString(33))
                    .openingDate(LocalDateTime.parse(rs.getString(17), inputFormatter))
                    .build())
                .accountHolder(AccountHolder.builder()
                    .id(rs.getLong(27))
                    .name(retrieveAccountHolderName(rs))
                    .build())
                .build();

    }

    /**
     * If the query contains account holder info, in case of organisation the name is stored in the name field,
     * in case of individual it is stored in first + last names.
     */
    private String retrieveAccountHolderName(ResultSet rs) throws SQLException {
        boolean hasAccountHolderInfo = rs.getMetaData().getColumnCount() > 21;
        if (!hasAccountHolderInfo) {
            return "";
        }
        String ahName = rs.getString(29);
        if (ahName != null) {
            return ahName;
        };
        return rs.getString(25) + " " + rs.getString(28);
    }
}
