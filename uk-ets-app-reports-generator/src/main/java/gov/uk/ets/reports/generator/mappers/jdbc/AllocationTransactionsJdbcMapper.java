package gov.uk.ets.reports.generator.mappers.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import gov.uk.ets.reports.generator.domain.Account;
import gov.uk.ets.reports.generator.domain.AllocationTransactionsReportData;
import gov.uk.ets.reports.generator.domain.Transaction;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Service
@RequiredArgsConstructor
@Log4j2
public class AllocationTransactionsJdbcMapper
    implements ReportDataMapper<AllocationTransactionsReportData>, RowMapper<AllocationTransactionsReportData> {

    private final JdbcTemplate jdbcTemplate;
    
    private static final String REPORT_QUERY = "select t.identifier as transaction_id, \r\n"
    		+ "t.type as transaction_type,  \r\n"
    		+ "objt.identifier as reverses_identifier,\r\n"
    		+ "subject.reversed_by_identifier,\r\n"
    		+ "t.quantity,\r\n"
    		+ "cast(t.attributes as json) ->> 'AllocationYear' as allocation_year,\r\n"
    		+ "cast(t.attributes as json) ->> 'AllocationType' as allocation_type,\r\n"
    		+ "ta.full_identifier as transferring_account_identifier,\r\n"
    		+ "ta.type_label as transferring_account_type,\r\n"
    		+ "ta.account_name as transferring_account_name,\r\n"
    		+ "tah.name as transferring_account_holder_name,\r\n"
    		+ "aa.full_identifier as acquiring_account_identifier,\r\n"
    		+ "aa.type_label as acquiring_account_type,\r\n"
    		+ "aa.account_name as acquiring_account_name,\r\n"
    		+ "aah.name as acquiring_account_holder_name,\r\n"
    		+ "t.started,\r\n"
    		+ "t.last_updated,\r\n"
    		+ "t.status as transaction_status\r\n"
    		+ "from transaction t\r\n"
    		+ "left join account ta on  ta.identifier = t.transferring_account_identifier \r\n"
    		+ "left join account_holder tah on ta.account_holder_id = tah.id\r\n"
    		+ "left join account aa on  aa.identifier = t.acquiring_account_identifier \r\n"
    		+ "left join account_holder aah on aa.account_holder_id = aah.id\r\n"
    		+ "left join transaction_connection subtc on subtc.type = 'REVERSES' and subtc.subject_transaction_id = t.id\r\n"
    		+ "left join transaction objt on objt.id = subtc.object_transaction_id\r\n"
    		+ "left join lateral\r\n"
    		+ "    (select subt.identifier as reversed_by_identifier\r\n"
    		+ "     from transaction_connection objtc, transaction subt\r\n"
    		+ "     where objtc.type = 'REVERSES' and objtc.object_transaction_id = t.id and subt.id = objtc.subject_transaction_id and subt.status  = 'COMPLETED') subject on 1=1\r\n"
    		+ "where t.type in ('AllocateAllowances', 'ExcessAllocation', 'ReverseAllocateAllowances')\r\n"
    		+ "order by t.last_updated desc";

    @Override
    public List<AllocationTransactionsReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {    	
    	return jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public AllocationTransactionsReportData mapRow(ResultSet rs, int i) throws SQLException {
        return
            AllocationTransactionsReportData.builder()
                .transaction(Transaction.builder()
                    .id(rs.getString("transaction_id"))
                    .type(rs.getString("transaction_type"))
                    .reversesIdentifier(rs.getString("reverses_identifier"))
                    .reversedByIdentifier(rs.getString("reversed_by_identifier"))
                    .quantity(rs.getLong("quantity"))
                    .lastUpdated(parseDate(rs.getString("last_updated")))
                    .transactionStart(parseDate(rs.getString("started")))
                    .status(rs.getString("transaction_status"))
                    .failureReasons("NOT PROVIDED")
                    .build())
                .transferringAccount(Account.builder()
                    .number(rs.getString("transferring_account_identifier"))
                    .name(rs.getString("transferring_account_name"))
                    .type(rs.getString("transferring_account_type"))
                    .accountHolderName(rs.getString("transferring_account_holder_name"))
                    .build())
                .acquiringAccount(Account.builder()
                    .number(rs.getString("acquiring_account_identifier"))
                    .name(rs.getString("acquiring_account_name"))
                    .type(rs.getString("acquiring_account_type"))
                    .accountHolderName(rs.getString("acquiring_account_holder_name"))
                    .build())
                .allocationType(rs.getString("allocation_type"))
                .allocationYear(rs.getInt("allocation_year"))
                .build();
    }
}
