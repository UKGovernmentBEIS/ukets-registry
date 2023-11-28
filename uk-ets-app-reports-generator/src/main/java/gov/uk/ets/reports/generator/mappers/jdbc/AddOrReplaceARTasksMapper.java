package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.AddOrReplaceARTasksReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class AddOrReplaceARTasksMapper implements ReportDataMapper<AddOrReplaceARTasksReportData>,
    RowMapper<AddOrReplaceARTasksReportData> {

    /**
     * @see <a href="https://stackoverflow.com/questions/24074820/weeks-between-two-dates-in-postgres">here</a>
     *
     * We start to cound the number of weeks from the week 0 when the task was initiated until current week
     * We always increase the week counter by 1, when every Monday starts
     * 604800 is the number of seconds in a week
     *
     * date_trunc('week', date) extracts the first date of the respective week of the date input
     * while extract(epoch from date_trunc('week', date)) extracts from teh above date the number of week counting
     * every week increese (by 1) from every Monday.
     *
     * @see <a href="https://www.postgresql.org/docs/9.4/datatype-json.html">here</a>
     *
     * t.difference::jsonb converts a text to a respective json type in postgres, while t.difference::jsonb ->> 'urid'
     * extracts field urid found in converted json object of t.difference::jsonb
     *
     */
    private static final String REPORT_QUERY = "" +
        "select\n" +
        "    t.request_identifier                                                                   as Task ,\n" +
        "    case\n" +
        "        when t.\"type\" = 'AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST' then 'Add AR'\n" +
        "        else 'Replace AR'\n" +
        "        end as Task_type,\n" +
        "    t.initiated_date                                                                       as Initiated,\n" +
        "    round((extract(epoch from date_trunc('week',now()::date)) \n" +
        "       - extract(epoch from date_trunc('week',t.initiated_date::date)))/604800)            as Task_weeks,\n" +
        "    ah.name                                                                                as AH_name,\n" +
        "    a.type_label                                                                           as Account_type,\n" +
        "    a.full_identifier                                                                      as Account_number,\n" +
        "    u.disclosed_name                                                                       as AR,\n" +
        "    t.difference::jsonb ->> 'urid'                                                         as URID,\n" +
        "    u.state                                                                                as Status,\n" +
        "    coalesce(nullif(u_cl.known_as,''), concat(u_cl.first_name, ' ', u_cl.last_name))       as Name_of_claimant,\n" +
        "    (\n" +
        "        select\n" +
        "            count(ta.id)\n" +
        "        from\n" +
        "            task ta\n" +
        "        where\n" +
        "                ta.user_id = t.user_id\n" +
        "          and\n" +
        "                ta.\"type\" = 'AR_REQUESTED_DOCUMENT_UPLOAD'\n" +
        "          and\n" +
        "                ta.id != t.id\n" +
        "          and\n" +
        "                ta.status = 'SUBMITTED_NOT_YET_APPROVED')                                  as Open_Doc_Requests,\n" +
        "    (\n" +
        "        select\n" +
        "            count(tas.id)\n" +
        "        from\n" +
        "            task tas\n" +
        "        where\n" +
        "                tas.user_id = t.user_id\n" +
        "          and\n" +
        "                tas.\"type\" = 'AR_REQUESTED_DOCUMENT_UPLOAD'\n" +
        "          and\n" +
        "                tas.id != t.id\n" +
        "          and\n" +
        "                tas.status = 'APPROVED')                                                   as Completed_Doc_Requests\n" +
        "from\n" +
        "    task t\n" +
        "        " +
        "left join account a on\n" +
        "            a.id = t.account_id\n" +
        "        " +
        "left join account_holder ah on\n" +
        "            ah.id = a.account_holder_id\n" +
        "        " +
        "left join users u on\n" +
        "            u.urid = t.difference::jsonb ->> 'urid'\n" +
        "        " +
        "left join users u_cl on\n" +
        "            u_cl.id = t.claimed_by\n" +
        "where\n" +
        "        t.\"type\" in ('AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST', 'AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST')\n" +
        "  and t.status = 'SUBMITTED_NOT_YET_APPROVED'\n" +
        "order by\n" +
        "  Task desc";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<AddOrReplaceARTasksReportData> mapData(ReportCriteria criteria) {
        return List.of();
    }

    @Override
    public List<AddOrReplaceARTasksReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public AddOrReplaceARTasksReportData mapRow(ResultSet resultSet, int i) throws SQLException {
        return
            AddOrReplaceARTasksReportData.builder()
                .taskRequestIdentifier(resultSet.getLong("Task"))
                .requestType(resultSet.getString("Task_type"))
                .initiatedDate(LocalDateTime.parse(resultSet.getString("Initiated"), formatter))
                .taskWeeks(resultSet.getLong("Task_weeks"))
                .accountHolderName(resultSet.getString("AH_name"))
                .accountType(resultSet.getString("Account_type"))
                .accountFullIdentifier(resultSet.getString("Account_number"))
                .disclosedName(resultSet.getString("AR"))
                .urid(resultSet.getString("URID"))
                .arUserStatus(resultSet.getString("Status"))
                .nameClaimant(resultSet.getString("Name_of_claimant"))
                .totalOpenDocRequests(resultSet.getLong("Open_Doc_Requests"))
                .totalCompletedDocRequests(resultSet.getLong("Completed_Doc_Requests"))
                .build();
    }
}
