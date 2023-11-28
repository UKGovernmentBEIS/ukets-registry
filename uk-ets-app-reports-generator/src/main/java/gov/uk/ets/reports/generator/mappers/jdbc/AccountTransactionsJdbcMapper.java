package gov.uk.ets.reports.generator.mappers.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import gov.uk.ets.reports.generator.domain.TransactionType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import gov.uk.ets.reports.generator.domain.AccountTransactionsReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class AccountTransactionsJdbcMapper implements ReportDataMapper<AccountTransactionsReportData>, RowMapper<AccountTransactionsReportData> {
    
    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY = "select a.account_name,ah.\"name\" ,t.last_updated , t.identifier , \r\n"
        + "case \r\n"
        + " when t.transferring_account_full_identifier  = ? then t.acquiring_account_full_identifier \r\n"
        + " when t.acquiring_account_full_identifier = ? then t.transferring_account_full_identifier \r\n"
        + "end as other_account_identifier,\r\n"
        + "t.\"type\" ,\r\n"
        + "string_agg(distinct t.unit_type ,',') as unit_type,\r\n"
        + "case \r\n"
        + " when t.transferring_account_full_identifier  = ? and t.\"type\" not in ('IssueOfAAUsAndRMUs','IssueAllowances','IssuanceCP0','IssuanceOfFormerEUA','IssuanceDecoupling','ConversionA','ConversionB','ConversionCP1','ConversionOfSurrenderedFormerEUA') then t.quantity \r\n"
        + "end as quantity_outgoing,\r\n"
        + "case \r\n"
        + " when t.acquiring_account_full_identifier = ? then t.quantity \r\n"
        + "end as quantity_incoming,\r\n"
        + "case \r\n"
        + " when t.unit_type = 'ALLOWANCE' then 'Not Applicable' \r\n"
        + " else string_agg(tb.project_number , ', ')\r\n"
        + "end as project_number,\r\n"
        + "case \r\n"
        + " when t.transferring_account_full_identifier  = ? then tab.transferring_account_balance \r\n"
        + " when t.acquiring_account_full_identifier = ? then tab.acquiring_account_balance \r\n"
        + "end as running_balance,\r\n"
        + "a.registry_account_type as report_registry_account_type,\r\n"
        + "a.kyoto_account_type as report_kyoto_account_type,\r\n"
        + "oa.registry_account_type other_registry_account_type,\r\n"
        + "oa.kyoto_account_type as other_kyoto_account_type,\r\n"
        + "oa.account_name as other_account_name,\r\n"
        + "objt.identifier as reverses_identifier,\r\n"
        + "subject.reversed_by_identifier\r\n"
        + "from \"transaction\" t inner join transaction_block tb on tb.transaction_id = t.id \r\n"
        + "inner join transaction_account_balance tab on tab.transaction_identifier = t.identifier \r\n"
        + "inner join account a on a.full_identifier = ? left join account_ownership ao on ao.account_id = a.id  left join account_holder ah on ah.id =ao.account_holder_id \r\n"
        + "left join account oa on (oa.full_identifier = t.transferring_account_full_identifier or oa.full_identifier = t.acquiring_account_full_identifier) and oa.full_identifier <> ?\r\n"
        + "left join transaction_connection subtc on subtc.\"type\" = 'REVERSES' and subtc.subject_transaction_id = t.id \r\n"
        + "left join transaction objt on objt.id = subtc.object_transaction_id \r\n"
        + "left join lateral \r\n"
        + "(select subt.identifier as reversed_by_identifier \r\n"
        + "from transaction_connection objtc, transaction subt \r\n"
        + "where objtc.\"type\" = 'REVERSES' and objtc.object_transaction_id = t.id and subt.id = objtc.subject_transaction_id and subt.\"status\"  = 'COMPLETED') subject on 1=1 \r\n"
        + "where t.\"status\"  = 'COMPLETED' and (t.transferring_account_full_identifier = ? or t.acquiring_account_full_identifier = ?)\r\n"
        + "group by a.account_name,ah.\"name\" ,a.registry_account_type , a.kyoto_account_type, oa.registry_account_type ,oa.kyoto_account_type , t.last_updated , t.identifier , other_account_identifier , t.\"type\" , t.unit_type , quantity_outgoing , quantity_incoming , tb.project_number ,running_balance,oa.account_name, objt.identifier, subject.reversed_by_identifier \r\n"
        + "order by last_updated desc";
        
    @Override
    public List<AccountTransactionsReportData> mapData(ReportCriteria criteria) {
        return List.of();
    }

    @Override
    public List<AccountTransactionsReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this , reportQueryInfo.getAccountFullIdentifier(),
            reportQueryInfo.getAccountFullIdentifier(),
            reportQueryInfo.getAccountFullIdentifier(),
            reportQueryInfo.getAccountFullIdentifier(),
            reportQueryInfo.getAccountFullIdentifier(),
            reportQueryInfo.getAccountFullIdentifier(),
            reportQueryInfo.getAccountFullIdentifier(),
            reportQueryInfo.getAccountFullIdentifier(),
            reportQueryInfo.getAccountFullIdentifier(),
            reportQueryInfo.getAccountFullIdentifier());
    }

    @Override
    public AccountTransactionsReportData mapRow(ResultSet rs, int rowNum) throws SQLException {

        return AccountTransactionsReportData
            .builder()
            .accountName(rs.getString("account_name"))
            .accountHolderName(rs.getString("name"))
            .completionDate(parseDate(rs.getString("last_updated")))
            .transactionIdentifier(rs.getString("identifier"))
            .otherAccountIdentifier(rs.getString("other_account_identifier"))
            .transactionType(TransactionType.valueOf(rs.getString("type")).getDefaultLabel())
            .unitType(rs.getString("unit_type"))
            .unitQuantityIncoming(rs.getLong("quantity_incoming"))
            .unitQuantityOutgoing(rs.getLong("quantity_outgoing"))
            .projectId(rs.getString("project_number"))
            .runningBalance(rs.getLong("running_balance"))
            .reportRegistryAccountType(rs.getString("report_registry_account_type"))
            .reportKyotoAccountType(rs.getString("report_kyoto_account_type"))
            .otherRegistryAccountType(rs.getString("other_registry_account_type"))
            .otherKyotoAccountType(rs.getString("other_kyoto_account_type"))
            .otherAccountName(rs.getString("other_account_name"))
            .reversesIdentifier(rs.getString("reverses_identifier"))
            .reversedByIdentifier(rs.getString("reversed_by_identifier"))
            .build();
    }
}
