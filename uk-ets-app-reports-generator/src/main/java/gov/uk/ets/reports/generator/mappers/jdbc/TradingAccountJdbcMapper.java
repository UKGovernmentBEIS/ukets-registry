package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.TradingAccountReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class TradingAccountJdbcMapper
        implements ReportDataMapper<TradingAccountReportData>, RowMapper<TradingAccountReportData> {

    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY = "" +
            "select     case when ah.type = 'ORGANISATION' or ah.type ='GOVERNMENT' then ah.name else CONCAT(ahr.first_name,' ',ahr.last_name) end as account_holder, \n" +
            "           case when ah.registration_number is null or ah.registration_number = '' then 'Not applicable' else ah.registration_number end as registration_number, \n"+
            "           CONCAT(co.line_1,' ',co.line_2,' ',co.line_3) as address, \n"+
            "           co.city as city, \n" +
            "           co.state_or_province as state_or_province, \n"+
            "           co.post_code as postcode, \n"+
            "           co.country as country, \n"+
            "           case when ac.account_status = 'CLOSED' then 'CLOSED' else 'OPEN' end as account_status, \n"+
            "           EXTRACT(YEAR FROM ac.opening_date) as open, \n"+
            "           '' as close, \n"+
            "           ac.sales_contact_email, \n"+
            "           ac.sales_contact_phone_number_country, \n"+
            "           ac.sales_contact_phone_number \n"+
            "from account as ac  \n" +
            "   inner join account_holder as ah \n" +
            "       on ac.account_holder_id = ah.id \n" +
            "   left join account_holder_representative ahr\n"+
            "       on ahr.account_holder_id = ah.id and ahr.account_contact_type = 'PRIMARY'\n"+
            "   inner join contact as co \n" +
            "       on ah.contact_id = co.id \n" +
            "where registry_account_type = 'TRADING_ACCOUNT' \n" +
            " and ac.account_status <>  'REJECTED' \n"+
            "order by account_holder";

    @Override
    public List<TradingAccountReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public TradingAccountReportData mapRow(ResultSet resultSet, int i) throws SQLException {
        return TradingAccountReportData.builder()
                .accountHolderName(resultSet.getString("account_holder"))
                .registrationNumber(resultSet.getString("registration_number"))
                .address(resultSet.getString("address"))
                .city(resultSet.getString("city"))
                .stateOrProvince(resultSet.getString("state_or_province"))
                .postcode(resultSet.getString("postcode"))
                .country(resultSet.getString("country"))
                .accountStatus(resultSet.getString("account_status"))
                .openYear(resultSet.getInt("open"))
                .close(resultSet.getString("close"))
                .salesContactEmail(resultSet.getString("sales_contact_email"))
                .salesContactPhone(StringUtils.isNotBlank(resultSet.getString("sales_contact_phone_number_country")) ?  StringUtils.trim(resultSet.getString("sales_contact_phone_number_country") + " " +resultSet.getString("sales_contact_phone_number")) : "")
                .build();
    }
}
