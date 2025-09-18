package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.TransactionsPublicReportData;
import gov.uk.ets.reports.generator.keycloak.KeycloakDbService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class TransactionsPublicReportJdbcMapper
    implements ReportDataMapper<TransactionsPublicReportData>, RowMapper<TransactionsPublicReportData> {

    private final KeycloakDbService keycloakDbService;
    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY = """
            SELECT
                t.identifier AS transfer_identifier,
                t.quantity,
                t.status,
                t.last_updated,
                ah.name AS transferring_account_holder_name,
                ta.public_identifier AS transferring_account_identifier,
                ta.registry_account_type AS transferring_account_type,
                aa.public_identifier AS receiving_account_identifier,
                aa.registry_account_type AS receiving_account_type,
                aah.name AS receiving_account_holder_name,
                t.type AS type_of_transfer,
                TO_CHAR(t.execution_date, 'YYYY-MM-DD') AS transaction_date,
                TO_CHAR(t.execution_date, 'HH24:MI:SS') AS transaction_time
            
            FROM transaction t
            
            INNER JOIN account ta ON
                ta.identifier = t.transferring_account_identifier
            INNER JOIN account_holder ah ON
                ta.account_holder_id = ah.id
            
            INNER JOIN account aa ON
                aa.identifier = t.acquiring_account_identifier
            INNER JOIN account_holder aah ON
                aa.account_holder_id = aah.id
            
            WHERE
                t.status = 'COMPLETED'
                AND (ta.registry_account_type IN ('OPERATOR_HOLDING_ACCOUNT', 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT', 'MARITIME_OPERATOR_HOLDING_ACCOUNT', 'TRADING_ACCOUNT', 'UK_AUCTION_DELIVERY_ACCOUNT')
                OR aa.registry_account_type IN ('OPERATOR_HOLDING_ACCOUNT', 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT', 'MARITIME_OPERATOR_HOLDING_ACCOUNT', 'TRADING_ACCOUNT', 'UK_AUCTION_DELIVERY_ACCOUNT'))
            
            AND EXISTS (
                SELECT 1
                FROM (SELECT ?::int AS year_param) AS year_table
                WHERE t.execution_date >=
                    CASE
                        WHEN year_param = 2025 THEN DATE '2021-01-01'
                        ELSE MAKE_DATE((year_param - 4), 5, 1)
                    END
                AND t.execution_date <
                    MAKE_DATE((year_param - 3), 5, 1)
            )
        """;

    @Override
    public List<TransactionsPublicReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this, reportQueryInfo.getYear());
    }

    @Override
    public TransactionsPublicReportData mapRow(ResultSet rs, int i) throws SQLException {
        return TransactionsPublicReportData.builder()
            .transferIdentifier(rs.getString("transfer_identifier"))
            .transferringAccountHolderName(rs.getString("transferring_account_holder_name"))
            .transferringAccountType(rs.getString("transferring_account_type"))
            .transferringAccountIdentifier(rs.getString("transferring_account_identifier"))
            .receivingAccountHolderName(rs.getString("receiving_account_holder_name"))
            .receivingAccountType(rs.getString("receiving_account_type"))
            .receivingAccountIdentifier(rs.getString("receiving_account_identifier"))
            .numberOfUKAllowancesTransferred(rs.getInt("quantity"))
            .typeOfTransfer(rs.getString("type_of_transfer"))
            .dateTransferCompleted(rs.getString("transaction_date"))
            .timeTransferCompleted(rs.getString("transaction_time"))
            .build();
    }

}
