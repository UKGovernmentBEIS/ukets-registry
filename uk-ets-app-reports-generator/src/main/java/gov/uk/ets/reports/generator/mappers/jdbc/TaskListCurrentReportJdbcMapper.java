package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.Util;
import gov.uk.ets.reports.generator.domain.RequestType;
import gov.uk.ets.reports.generator.domain.TaskListCurrentReportData;
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
public class TaskListCurrentReportJdbcMapper
    implements ReportDataMapper<TaskListCurrentReportData>, RowMapper<TaskListCurrentReportData> {

    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY = "select t.request_identifier as task_id,\n" +
        " t.type as task_type,\n" +
        " p.priority,\n" +
        " case when ui.known_as <> '' then ui.known_as else concat_ws(' ', ui.first_name, ui.last_name) end as initiator,\n" +
        " ui.urid as initiator_uid,\n" +
        " case when uif.user_id is null then 'Yes' else 'No' end as user_initiated," +
        " t.initiated_date as initiated,\n" +
        " TRUNC(DATE_PART('day', now() - t.initiated_date)/7) as age,\n" +
        " case when uc.known_as <> '' then uc.known_as else concat_ws(' ', uc.first_name, uc.last_name) end as claimant,\n" +
        " ta.initial_assignment as ownership_date,\n" +
        " docs.open_document_requests,\n" +
        " docs.completed_document_requests,\n" +
        " COALESCE(DATE_PART('day', t.initiated_date - ta.initial_assignment)::text, 'Not Allocated') AS work_allocation_lag_days,\n" +
        " t.deadline,\n" +
        " case when t.type = 'ACCOUNT_OPENING_REQUEST' \n" +
        "  then case when aot.ah_type = 'INDIVIDUAL' then concat_ws(' ', aot.ah_first_name, aot.ah_last_name) else aot.ah_name end\n" +
        "  else case when ah.type = 'INDIVIDUAL' then concat_ws(' ', ah.first_name, ah.last_name) else ah.name end \n" +
        "  end as account_holder," +
        " case when t.type = 'ACCOUNT_OPENING_REQUEST' then aot.account_type  else a.type_label end as account_type,\n" +
        " a.full_identifier as account_number,\n" +
        " ars.enrolled_ars,\n" +
        " ars.validated_ars,\n" +
        " ars.suspended_ars,\n" +
        " ars.total_ars,\n" +
        " case when t.type = 'ACCOUNT_OPENING_REQUEST' then aot.ar_nominations  else arn.ar_nominations end as ar_nominations,\n" +
        " u.num_of_tasks as user_tasks,\n" +
        " a.compliance_status as dynamic_surrender_status,\n" +
        " case when u.known_as <> '' then u.known_as else concat_ws(' ', u.first_name, u.last_name) end as user,\n" +
        " u.urid as user_id,\n" +
        " u.state as status\n" +
        " -- last_signed_id is populated using the KeycloakDbService at service layer\n" +
        " from task t \n" +
        " join users ui on t.initiated_by = ui.id \n" +
        " left join users uc on t.claimed_by = uc.id\n" +
        " left join account a on t.account_id = a.id\n" +
        " left join account_holder ah on a.account_holder_id  = ah.id\n" +
        " -- ownership_date\n" +
        " left join lateral (\n" +
        "  select task_id, min(assignment_date) as initial_assignment from task_assignment group by task_id\n" +
        " ) ta on ta.task_id = t.id\n" +
        " -- User-Initiated\n" +
        " left join lateral (\n" +
        "  select user_id from user_role_mapping urm\n" +
        "  join iam_user_role iur on iur.id = urm.role_id and iur.role_name in ('junior-registry-administrator', 'senior-registry-administrator')\n" +
        " ) uif on uif.user_id = t.initiated_by\n" +
        " -- User's task\n" +
        " left join lateral (\n" +
        "  SELECT uu.id, uu.urid, uu.known_as, uu.first_name, uu.last_name, uu.state, count(1) as num_of_tasks\n" +
        "  from task_search_metadata tsm \n" +
        "  join task tt on tsm.task_id = tt.id\n" +
        "  join users uu on uu.urid = SUBSTRING(tsm.metadata_value, 0, POSITION(',' in tsm.metadata_value))\n" +
        "  where tsm.metadata_name = 'USER_ID_NAME_KNOWN_AS' and tt.status = 'SUBMITTED_NOT_YET_APPROVED'\n" +
        "  group by uu.id, uu.urid, uu.known_as, uu.first_name, uu.last_name, uu.state" +
        " ) u on t.user_id = u.id\n" +
        " -- AR nominations\n" +
        " left join lateral (\n" +
        "  select ac.id as account_id, count(1) as ar_nominations\n" +
        "  from task ttt join account ac on ttt.account_id = ac.id\n" +
        "  where ttt.status = 'SUBMITTED_NOT_YET_APPROVED' \n" +
        "  and ttt.type in ('AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST', 'AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST')\n" +
        "  group by ac.id\n" +
        " ) arn on arn.account_id = a.id\n" +
        " -- Account openings\n" +
        " left join lateral (\n" +
        "  select id, \n" +
        "  difference::json -> 'accountHolder' ->> 'type' as ah_type, \n" +
        "  difference::json -> 'accountHolder' -> 'details' ->> 'name' as ah_name, \n" +
        "  difference::json -> 'accountHolder' -> 'details' ->> 'firstName' as ah_first_name, \n" +
        "  difference::json -> 'accountHolder' -> 'details' ->> 'lastName' as ah_last_name, \n" +
        "  difference::json ->> 'accountType' as account_type, \n" +
        "  JSON_ARRAY_LENGTH(difference::json -> 'authorisedRepresentatives') as ar_nominations\n" +
        "  from task where type = 'ACCOUNT_OPENING_REQUEST' and status = 'SUBMITTED_NOT_YET_APPROVED'\n" +
        " ) as aot on aot.id = t.id" +
        " -- AR counts\n" +
        " left join lateral (\n" +
        "  select aa.account_id, \n" +
        "  count(1) as total_ars,\n" +
        "  sum(case when uar.state = 'ENROLLED' then 1 else 0 end) as enrolled_ars,\n" +
        "  sum(case when uar.state = 'VALIDATED' then 1 else 0 end) as validated_ars,\n" +
        "  sum(case when uar.state = 'SUSPENDED' then 1 else 0 end) as suspended_ars\n" +
        "  from account_access aa join users uar on aa.user_id = uar.id \n" +
        "  where aa.access_right <> 'ROLE_BASED' and aa.state in ('ACTIVE', 'SUSPENDED')\n" +
        "  group by aa.account_id \n" +
        " ) ars on ars.account_id = a.id\n" +
        " -- doc counts\n" +
        " left join lateral (\n" +
        "  select tdoc.parent_task_id, \n" +
        "  sum(case when tdoc.status  = 'SUBMITTED_NOT_YET_APPROVED' then 1 else 0 end) as open_document_requests,\n" +
        "  sum(case when tdoc.status in ('APPROVED', 'REJECTED') then 1 else 0 end) as completed_document_requests\n" +
        "  from task tdoc\n" +
        "  where parent_task_id is not null\n" +
        "  group by tdoc.parent_task_id \n" +
        " ) as docs on docs.parent_task_id = t.id\n" +
        " -- priority\n" +
        " left join lateral (\n" +
        "  select tp.id as task_id,\n" +
        "  COALESCE(\n" +
        "   case when tp.type in ('LOST_TOKEN', 'LOST_PASSWORD_AND_TOKEN', 'CHANGE_TOKEN') then 1 end, \n" +
        "   case when tp.type in ('AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST', 'AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST') \n" +
        "   and ap.registry_account_type in ('OPERATOR_HOLDING_ACCOUNT', 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT')\n" +
        "   and ap.compliance_status in ('B', 'C') then 2 end,\n" +
        "   case when tp.type in ('AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST', 'AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST') \n" +
        "   and ap.registry_account_type in ('OPERATOR_HOLDING_ACCOUNT', 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT')\n" +
        "   and ap.compliance_status = 'A' then 3 end,\n" +
        "   case when tp.type in ('AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST', 'AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST') \n" +
        "   and (ap.registry_account_type = 'TRADING_ACCOUNT' or ap.kyoto_account_type in ('PERSON_HOLDING_ACCOUNT', 'FORMER_OPERATOR_HOLDING_ACCOUNT'))\n" +
        "   and ap.compliance_status is null then 4 end, \n" +
        "   case when tp.type in ('AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST', 'AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST') \n" +
        "   and ap.registry_account_type in ('OPERATOR_HOLDING_ACCOUNT', 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT')\n" +
        "   and ap.compliance_status = 'NOT_APPLICABLE' then 4 end,\n" +
        "   case when tp.type = 'AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST'\n" +
        "   and ap.registry_account_type in ('OPERATOR_HOLDING_ACCOUNT', 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT')\n" +
        "   and ap.compliance_status = 'EXCLUDED' then 4 end,\n" +
        "   case when tp.type = 'AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST'\n" +
        "   and ap.registry_account_type in ('OPERATOR_HOLDING_ACCOUNT', 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT')\n" +
        "   and ap.compliance_status = 'EXCLUDED' then 5 end,\n" +
        "   case when tp.type = 'ACCOUNT_OPENING_REQUEST'\n" +
        "   and tp.difference::jsonb ->> 'accountType' in ('TRADING_ACCOUNT', 'PERSON_HOLDING_ACCOUNT', 'FORMER_OPERATOR_HOLDING_ACCOUNT') then 5 end,\n" +
        "   6\n" +
        "  ) as priority\n" +
        " from task tp \n" +
        " left join account ap on tp.account_id = ap.id\n" +
        " ) p on p.task_id = t.id\n" +
        " where t.status = 'SUBMITTED_NOT_YET_APPROVED'" +
        " and t.type not in ('AH_REQUESTED_DOCUMENT_UPLOAD', 'AR_REQUESTED_DOCUMENT_UPLOAD', 'ADD_TRUSTED_ACCOUNT_REQUEST', 'DELETE_TRUSTED_ACCOUNT_REQUEST', 'TRANSACTION_REQUEST', 'ALLOCATION_TABLE_UPLOAD_REQUEST', 'PAYMENT_REQUEST')" +
        " order by t.request_identifier";

    @Override
    public List<TaskListCurrentReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public TaskListCurrentReportData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return TaskListCurrentReportData.builder()
            .taskId(rs.getLong("task_id"))
            .taskType(RequestType.getLabel(rs.getString("task_type")))
            .priority(rs.getInt("priority"))
            .initiator(rs.getString("initiator"))
            .initiatorUid(rs.getString("initiator_uid"))
            .userInitiated(rs.getString("user_initiated"))
            .initiated(prettyDate(rs.getString("initiated")))
            .age(rs.getInt("age"))
            .claimant(rs.getString("claimant"))
            .ownershipDate(prettyDate(rs.getString("ownership_date")))
            .openDocumentRequests(rs.getInt("open_document_requests"))
            .completedDocumentRequests(rs.getInt("completed_document_requests"))
            .workAllocationLagDays(rs.getString("work_allocation_lag_days"))
            .deadline(prettyDate(rs.getString("deadline")))
            .accountHolder(rs.getString("account_holder"))
            .accountType(rs.getString("account_type"))
            .accountNumber(rs.getString("account_number"))
            .enrolledARs(Util.getNullableInteger(rs,"enrolled_ars"))
            .validatedARs(Util.getNullableInteger(rs,"validated_ars"))
            .suspendedARs(Util.getNullableInteger(rs,"suspended_ars"))
            .totalARs(Util.getNullableInteger(rs,"total_ars"))
            .arNominations(Util.getNullableInteger(rs,"ar_nominations"))
            .userTasks(Util.getNullableInteger(rs,"user_tasks"))
            .dynamicSurrenderStatus(rs.getString("dynamic_surrender_status"))
            .user(rs.getString("user"))
            .userID(rs.getString("user_id"))
            .status(rs.getString("status"))
            .build();
    }

}
