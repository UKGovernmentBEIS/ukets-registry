package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.Util;
import gov.uk.ets.reports.generator.domain.RequestType;
import gov.uk.ets.reports.generator.domain.TaskListHistoryReportData;
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
public class TaskListHistoryReportJdbcMapper
        implements ReportDataMapper<TaskListHistoryReportData>, RowMapper<TaskListHistoryReportData> {

    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY = "SELECT \n" +
            "t.request_identifier as task_id,\n" +
            "t.type as task_type,\n" +
            "case when initiated_by.known_as is null or initiated_by.known_as = '' then concat(initiated_by.first_name, ' ', initiated_by.last_name) else initiated_by.known_as end as initiator,\n" +
            "initiated_by.urid as initiator_uid,\n" +
            "case when uif.user_id is null then 'Yes' else 'No' end as user_initiated,\n" +
            "t.initiated_date as initiated,\n" +
            "TRUNC(DATE_PART('day', now() - t.initiated_date)/7) as age,\n" +
            "case when claimed_by.known_as is null or claimed_by.known_as = '' then concat(claimed_by.first_name, ' ', claimed_by.last_name) else claimed_by.known_as end as claimant,\n" +
            "ta_init.initial_assignment as ownership_date,\n" +
            "docs.completed_document_requests as completed_document_requests,\n" +
            "ABS(DATE_PART('day', docs.first_doc_request_date - ta_init.initial_assignment)) as work_initiation_lag,\n" +
            "wp.username as work_processor,\n" +
            "t.deadline,\n" +
            "t.completed_date as completion_date,\n" +
            "'COMPLETED' as task_status,\n" +
            "t.status as task_outcome,\n" +
            "toc.description as task_outcome_comment,\n" +
            "ah.name as account_holder,\n" +
            "a.registry_account_type as account_type,\n" +
            "a.identifier as account_number,\n" +
            "case when u.known_as is null or u.known_as = '' then concat(u.first_name, ' ', u.last_name) else u.known_as end as user,\n" +
            "u.urid as user_uid\n" +
            "FROM public.task as t\n" +
            "left join account a on a.id = t.account_id\n" +
            "left join users u on u.id = t.user_id\n" +
            "left join users initiated_by on initiated_by.id = t.initiated_by\n" +
            "left join users claimed_by on claimed_by.id = t.claimed_by\n" +
            "left join users completed_by on completed_by.id = t.claimed_by\n" +
            "left join account_holder ah on ah.id = a.account_holder_id\n" +
            "left join lateral (\n" +
            "  select task_id, min(assignment_date) as initial_assignment \n" +
            "  from task_assignment \n" +
            "  group by task_id \n" +
            ") ta_init on ta_init.task_id = t.id\n" +
            "left join lateral (\n" +
            "  select user_id from user_role_mapping urm\n" +
            "  join iam_user_role iur on iur.id = urm.role_id and iur.role_name in ('junior-registry-administrator', 'senior-registry-administrator')\n" +
            ") uif on uif.user_id = t.initiated_by\n" +
            "left join lateral (\n" +
            "  select distinct on (ta.task_id) task_id,\n" +
            "  case when u.known_as is null or u.known_as = '' then concat(u.first_name, ' ', u.last_name) else u.known_as end as username\n" +
            "  from task_assignment as ta\n" +
            "  left join users u on u.urid = ta.urid\n" +
            "  where roles in ('junior-registry-administrator', 'senior-registry-administrator')\n" +
            "  order by task_id, ta.roles, ta.assignment_date\n" +
            ") wp on wp.task_id = t.id\n" +
            "left join lateral (\n" +
            "  select domain_id, description\n" +
            "  from domain_event\n" +
            "  where domain_action like '%completed. (comment)'\n" +
            ") as toc on t.request_identifier::varchar = toc.domain_id\n" +
            "left join lateral (\n" +
            "  select tdoc.parent_task_id, \n" +
            "  min(tdoc.initiated_date) as first_doc_request_date,\n" +
            "  sum(case when tdoc.status in ('APPROVED', 'REJECTED') then 1 else 0 end) as completed_document_requests\n" +
            "  from task tdoc\n" +
            "  where parent_task_id is not null\n" +
            "  group by tdoc.parent_task_id\n" +
            ") as docs on docs.parent_task_id = t.id\n" +
            "WHERE \n" +
            "  DATE_PART('day', now() - t.initiated_date) < 365 and\t-- Last year\n" +
            "  (t.status = 'APPROVED' or t.status = 'REJECTED') and\t-- Only completed\n" +
            "  t.type not in ('AH_REQUESTED_DOCUMENT_UPLOAD', 'AR_REQUESTED_DOCUMENT_UPLOAD', 'ADD_TRUSTED_ACCOUNT_REQUEST', 'DELETE_TRUSTED_ACCOUNT_REQUEST', 'TRANSACTION_REQUEST', 'ALLOCATION_TABLE_UPLOAD_REQUEST')\t-- Only RA tasks\n" +
            "ORDER BY t.request_identifier ASC";

    @Override
    public List<TaskListHistoryReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public TaskListHistoryReportData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return TaskListHistoryReportData.builder()
                .taskId(rs.getLong("task_id"))
                .taskType(RequestType.getLabel(rs.getString("task_type")))
                .initiator(rs.getString("initiator"))
                .initiatorUid(rs.getString("initiator_uid"))
                .userInitiated(rs.getString("user_initiated"))
                .initiated(prettyDate(rs.getString("initiated")))
                .age(rs.getInt("age"))
                .claimant(rs.getString("claimant"))
                .ownershipDate(prettyDate(rs.getString("ownership_date")))
                .completedDocumentRequests(rs.getInt("completed_document_requests"))
                .workInitiationLag(Util.getNullableInteger(rs, "work_initiation_lag"))
                .workProcessor(rs.getString("work_processor"))
                .deadline(prettyDate(rs.getString("deadline")))
                .taskCompletionDate(prettyDate(rs.getString("completion_date")))
                .taskStatus(rs.getString("task_status"))
                .taskOutcome(rs.getString("task_outcome"))
                .taskOutcomeComment(rs.getString("task_outcome_comment"))
                .accountHolder(rs.getString("account_holder"))
                .accountType(rs.getString("account_type"))
                .accountNumber(rs.getString("account_number"))
                .user(rs.getString("user"))
                .userUid(rs.getString("user_uid"))
                .build();
    }
}
