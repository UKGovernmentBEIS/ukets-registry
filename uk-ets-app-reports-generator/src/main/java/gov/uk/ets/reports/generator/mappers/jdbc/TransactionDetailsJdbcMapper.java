package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.model.ReportRequestingRole;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import gov.uk.ets.reports.generator.domain.TransactionDetailsReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionDetailsJdbcMapper
    implements
        ReportDataMapper<TransactionDetailsReportData>, RowMapper<TransactionDetailsReportData>  {


    private static final String REPORT_QUERY = "select\r\n"
        + "    t.identifier ,\r\n"
        + "    t.status ,\r\n"
        + "    t.last_updated ,\r\n"
        + "    case \r\n"
        + "        when ta.id in (select account_id from account_access ac inner join users u on u.id = ac.user_id where u.urid = :urid and ac.access_right not in (:accessRight)) then ah.name \r\n"
        + "    end transferring_account_holder_name ,\r\n"
        + "    case\r\n"
        + "        when ta.registry_account_type in ('UK_TOTAL_QUANTITY_ACCOUNT', 'UK_AUCTION_ACCOUNT', 'UK_ALLOCATION_ACCOUNT', 'UK_NEW_ENTRANTS_RESERVE_ACCOUNT', 'UK_AVIATION_ALLOCATION_ACCOUNT', 'UK_DELETION_ACCOUNT', 'UK_SURRENDER_ACCOUNT', 'UK_MARKET_STABILITY_MECHANISM_ACCOUNT', 'UK_GENERAL_HOLDING_ACCOUNT', 'NATIONAL_HOLDING_ACCOUNT') then ta.account_name\r\n"
        + "        when ta.registry_account_type = 'NONE'\r\n"
        + "        and ta.kyoto_account_type in ('PARTY_HOLDING_ACCOUNT', 'PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT', 'NET_SOURCE_CANCELLATION_ACCOUNT', 'NON_COMPLIANCE_CANCELLATION_ACCOUNT', 'VOLUNTARY_CANCELLATION_ACCOUNT', 'MANDATORY_CANCELLATION_ACCOUNT', 'ARTICLE_3_7_TER_CANCELLATION_ACCOUNT', 'AMBITION_INCREASE_CANCELLATION_ACCOUNT', 'RETIREMENT_ACCOUNT', 'TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY', 'LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY', 'LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE', 'LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT') then ta.account_name\r\n"
        + "        else t.transferring_account_full_identifier\r\n"
        + "    end transferring_account_full_identifier,\r\n"
        + "    case \r\n"
        + "        when aa.id in (select account_id from account_access ac inner join users u on u.id = ac.user_id where u.urid = :urid and ac.access_right not in (:accessRight)) then aah.name \r\n"
        + "    end acquiring_account_holder_name ,\r\n"
        + "    case\r\n"
        + "        when aa.registry_account_type in ('UK_TOTAL_QUANTITY_ACCOUNT', 'UK_AUCTION_ACCOUNT', 'UK_ALLOCATION_ACCOUNT', 'UK_NEW_ENTRANTS_RESERVE_ACCOUNT', 'UK_AVIATION_ALLOCATION_ACCOUNT', 'UK_DELETION_ACCOUNT', 'UK_SURRENDER_ACCOUNT', 'UK_MARKET_STABILITY_MECHANISM_ACCOUNT', 'UK_GENERAL_HOLDING_ACCOUNT', 'NATIONAL_HOLDING_ACCOUNT') then aa.account_name\r\n"
        + "        when aa.registry_account_type = 'NONE'\r\n"
        + "        and aa.kyoto_account_type in ('PARTY_HOLDING_ACCOUNT', 'PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT', 'NET_SOURCE_CANCELLATION_ACCOUNT', 'NON_COMPLIANCE_CANCELLATION_ACCOUNT', 'VOLUNTARY_CANCELLATION_ACCOUNT', 'MANDATORY_CANCELLATION_ACCOUNT', 'ARTICLE_3_7_TER_CANCELLATION_ACCOUNT', 'AMBITION_INCREASE_CANCELLATION_ACCOUNT', 'RETIREMENT_ACCOUNT', 'TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY', 'LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY', 'LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE', 'LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT') then aa.account_name\r\n"
        + "        else t.acquiring_account_full_identifier\r\n"
        + "    end acquiring_account_full_identifier,\r\n"
        + "    t.type ,\r\n"
        + "    t.reference,\r\n"
        + "    tb.unit_type ,\r\n"
        + "    tb.project_number ,\r\n"
        + "    tb.applicable_period ,\r\n"
        + "    sum(tb.end_block-tb.start_block + 1) as quantity\r\n"
        + "from\r\n"
        + "    transaction t\r\n"
        + "inner join transaction_block tb on\r\n"
        + "    tb.transaction_id = t.id\r\n"
        + "left join account ta on\r\n"
        + "    ta.identifier = t.transferring_account_identifier\r\n"
        + "left join account_ownership ao on\r\n"
        + "    ao.account_id = ta.id and ao.status = 'ACTIVE'\r\n"
        + "left join account_holder ah on\r\n"
        + "    ao.account_holder_id = ah.id\r\n"
        + "left join account aa on\r\n"
        + "    aa.identifier = t.acquiring_account_identifier\r\n"
        + "left join account_ownership aao on\r\n"
        + "    aao.account_id = aa.id and aao.status = 'ACTIVE'\r\n"
        + "left join account_holder aah on\r\n"
        + "    aao.account_holder_id = aah.id\r\n"
        + "where\r\n"
        + "    t.identifier = :transactionIdentifier\r\n"
        + "group by\r\n"
        + "    t.identifier ,\r\n"
        + "    t.status ,\r\n"
        + "    t.last_updated ,\r\n"
        + "    ta.registry_account_type,\r\n"
        + "    ta.kyoto_account_type,\r\n"
        + "    ta.account_name ,\r\n"
        + "    ta.id ,\r\n"
        + "    ah.name ,\r\n"
        + "    aah.name ,\r\n"
        + "    aa.id ,\r\n"
        + "    aa.registry_account_type,\r\n"
        + "    aa.kyoto_account_type,\r\n"
        + "    aa.account_name,\r\n"
        + "    t.transferring_account_full_identifier ,\r\n"
        + "    t.acquiring_account_full_identifier ,\r\n"
        + "    t.type ,\r\n"
        + "    t.reference,\r\n"
        + "    tb.unit_type ,\r\n"
        + "    tb.project_number ,\r\n"
        + "    tb.applicable_period"; 
    
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<TransactionDetailsReportData> mapData(
        ReportQueryInfoWithMetadata reportQueryInfo) {

        String ignoreAccessRight = ignoreAccessRightBasedOnUserRole(reportQueryInfo.getRequestingRole());

        SqlParameterSource namedParameters = new MapSqlParameterSource()
            .addValue("urid", reportQueryInfo.getUrid())
            .addValue("accessRight", ignoreAccessRight)
            .addValue("transactionIdentifier", reportQueryInfo.getTransactionIdentifier());

        return jdbcTemplate.query(REPORT_QUERY, namedParameters, this);
    }

    @Override
    public TransactionDetailsReportData mapRow(ResultSet rs, int rowNum)
        throws SQLException {
        return TransactionDetailsReportData
            .builder()
            .identifier(rs.getString("identifier"))
            .status(rs.getString("status"))
            .lastUpdateDate(parseDate(rs.getString("last_updated")))
            .transferringAccountHolder(rs.getString("transferring_account_holder_name"))
            .transferringFullIdentifier(rs.getString("transferring_account_full_identifier"))
            .acquiringAccountHolder(rs.getString("acquiring_account_holder_name"))
            .acquiringFullIdentifier(rs.getString("acquiring_account_full_identifier"))
            .reference(rs.getString("reference"))
            .type(rs.getString("type"))
            .unitType(rs.getString("unit_type"))
            .projectNumber(rs.getString("project_number"))
            .applicablePeriod(rs.getString("applicable_period"))
            .quantity(rs.getLong("quantity"))
            .build();
    }

    private String ignoreAccessRightBasedOnUserRole(ReportRequestingRole role) {
        return role == ReportRequestingRole.authority ? "ROLE_BASED" : "";
    }
}
