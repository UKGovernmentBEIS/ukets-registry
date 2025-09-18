package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.ComplianceManagementReportData;
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
public class ComplianceManagementReportJdbcMapper
    implements ReportDataMapper<ComplianceManagementReportData>, RowMapper<ComplianceManagementReportData> {

    private final KeycloakDbService keycloakDbService;
    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY = """
                SELECT account_full_identifier,
                       regulator,
                       ah_name,
                       account_name,
                       account_type,
                       account_status,
                       account_holder_id,
                       account_opening_date,
                       account_closing_date,
                       current_surrender_status,
                       transactions_off_the_tal_allowed,
                       installation_name,
                       identifier,
                       permit_or_monitoring_plan_id,
                       start_year,
                       end_year,
                       dynamic_compliance_status,
                       ars.enrolled_ars,
                       ars.validated_ars,
                       ars.suspended_ars,
                       ars.total_ars,
                       ars.total_ars_uris,
                       arc.ar_changes_count,
                       arc.ar_changes_ids,
                       arc.ar_changes_claimants,
                       ub_summary.totalAvailable,
                       ub_summary.totalReserved,
                       MAX(surrender_transaction_pending) AS surrender_transaction_pending,
                       MAX(receive_allowances_pending) AS receive_allowances_pending,
                       approval_second_ar_required,
                       transfers_outside_tal,
                       single_person_approval_required,
                       MAX(CASE WHEN year = 2021 THEN verified_emissions_str END) AS verified_emissions_2021,
                       MAX(CASE WHEN year = 2022 THEN verified_emissions_str END) AS verified_emissions_2022,
                       MAX(CASE WHEN year = 2023 THEN verified_emissions_str END) AS verified_emissions_2023,
                       MAX(CASE WHEN year = 2024 THEN verified_emissions_str END) AS verified_emissions_2024,
                       MAX(CASE WHEN year = 2025 THEN verified_emissions_str END) AS verified_emissions_2025,
                       MAX(CASE WHEN year = 2026 THEN verified_emissions_str END) AS verified_emissions_2026,
                       MAX(CASE WHEN year = 2027 THEN verified_emissions_str END) AS verified_emissions_2027,
                       MAX(CASE WHEN year = 2028 THEN verified_emissions_str END) AS verified_emissions_2028,
                       MAX(CASE WHEN year = 2029 THEN verified_emissions_str END) AS verified_emissions_2029,
                       MAX(CASE WHEN year = 2030 THEN verified_emissions_str END) AS verified_emissions_2030,
                       SUM(COALESCE(CASE WHEN year BETWEEN 2021 AND 2030 THEN verified_emissions END, 0)) AS cum_verified_emissions,
                       SUM(COALESCE(CASE WHEN year BETWEEN 2021 AND 2030 THEN surrendered_emissions END, 0)) AS cum_surrendered_emissions,
                       MAX(CASE WHEN year = 2021 THEN compliance_status END) AS static_surrender_status_2021,
                       MAX(CASE WHEN year = 2022 THEN compliance_status END) AS static_surrender_status_2022,
                       MAX(CASE WHEN year = 2023 THEN compliance_status END) AS static_surrender_status_2023,
                       MAX(CASE WHEN year = 2024 THEN compliance_status END) AS static_surrender_status_2024,
                       MAX(CASE WHEN year = 2025 THEN compliance_status END) AS static_surrender_status_2025,
                       MAX(CASE WHEN year = 2026 THEN compliance_status END) AS static_surrender_status_2026,
                       MAX(CASE WHEN year = 2027 THEN compliance_status END) AS static_surrender_status_2027,
                       MAX(CASE WHEN year = 2028 THEN compliance_status END) AS static_surrender_status_2028,
                       MAX(CASE WHEN year = 2029 THEN compliance_status END) AS static_surrender_status_2029,
                       MAX(CASE WHEN year = 2030 THEN compliance_status END) AS static_surrender_status_2030

                FROM (
                    WITH years AS (
                        SELECT id, year FROM allocation_year ORDER BY year ASC
                    ),
                    surrender_allowances AS (
                        SELECT years.year, aa.id, COALESCE(SUM(t.quantity), 0) AS surrenders,
                               CASE
                                   WHEN EXISTS (
                                       SELECT 1
                                       FROM transaction t2
                                       WHERE t2.transferring_account_identifier = aa.identifier
                                         AND t2.type = 'SurrenderAllowances'
                                         AND t2.status NOT IN ('COMPLETED', 'FAILED', 'REJETED')
                                   ) THEN 'Yes'
                                   ELSE 'No'
                               END AS surrender_transaction_pending,
                               CASE
                                   WHEN EXISTS (
                                       SELECT 1
                                       FROM transaction t2
                                       WHERE t2.acquiring_account_identifier = aa.identifier
                                         AND t2.status NOT IN ('COMPLETED', 'FAILED', 'REJETED')
                                   ) THEN 'Yes'
                                   ELSE 'No'
                               END AS receive_allowances_pending
                        FROM transaction t
                                 INNER JOIN account aa ON t.transferring_account_identifier = aa.identifier
                                 LEFT JOIN installation_ownership io2 ON io2.account_id = aa.id
                                 LEFT JOIN installation ins ON io2.installation_id = ins.compliant_entity_id
                                 CROSS JOIN years
                        WHERE t.type = 'SurrenderAllowances'
                          AND t.status = 'COMPLETED'
                          AND execution_date >= TO_TIMESTAMP(CONCAT(years.year, '-05-01'), 'YYYY-MM-DD')
                          AND execution_date < TO_TIMESTAMP(CONCAT(years.year + 1, '-05-01'), 'YYYY-MM-DD')
                        GROUP BY io2.installation_id, aa.id, years.year
                    ),
                    reverse_allowances AS (
                        SELECT years.year, aa.id, COALESCE(SUM(t.quantity), 0) AS reversals
                        FROM transaction t
                                 INNER JOIN account aa ON t.acquiring_account_identifier = aa.identifier
                                 LEFT JOIN installation_ownership io2 ON io2.account_id = aa.id
                                 LEFT JOIN installation ins ON io2.installation_id = ins.compliant_entity_id
                                 CROSS JOIN years
                        WHERE t.type = 'ReverseSurrenderAllowances'
                          AND t.status = 'COMPLETED'
                          AND execution_date >= TO_TIMESTAMP(CONCAT(years.year, '-05-01'), 'YYYY-MM-DD')
                          AND execution_date < TO_TIMESTAMP(CONCAT(years.year + 1, '-05-01'), 'YYYY-MM-DD')
                        GROUP BY io2.installation_id, aa.id, years.year
                    ),
                    account_data_per_year AS (
                        SELECT years.year AS year,
                               ce.regulator,
                               ah.name AS ah_name,
                               ah.identifier AS account_holder_id,
                               a.id AS account_id,
                               a.identifier AS account_identifier,
                               a.full_identifier AS account_full_identifier,
                               a.account_name,
                               a.opening_date AS account_opening_date,
                               a.closing_date AS account_closing_date,
                               a.compliance_status AS current_surrender_status,
                               a.transfers_outside_tal AS transactions_off_the_tal_allowed,
                               a.registry_account_type AS account_type,
                               a.account_status,
                               a.approval_second_ar_required,
                               a.transfers_outside_tal,
                               a.single_person_approval_required,
                               ce.identifier,
                               CASE
                                    WHEN a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' 
                                        THEN (SELECT installation_name FROM installation i WHERE i.compliant_entity_id = ce.id)
                                    ELSE ''
                               END AS installation_name,
                               CASE
                                    WHEN a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT'
                                        THEN (SELECT permit_identifier FROM installation i WHERE i.compliant_entity_id = ce.id)
                                    WHEN a.registry_account_type = 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT'
                                        THEN (SELECT monitoring_plan_identifier FROM aircraft_operator ao WHERE ao.compliant_entity_id = ce.id)
                                    WHEN a.registry_account_type = 'MARITIME_OPERATOR_HOLDING_ACCOUNT'
                                        THEN (SELECT maritime_monitoring_plan_identifier FROM maritime_operator mo WHERE mo.compliant_entity_id = ce.id)
                               END AS permit_or_monitoring_plan_id,
                               ce.start_year,
                               ce.end_year,
                               a.compliance_status AS dynamic_compliance_status,
                               scs.compliance_status AS compliance_status,
                               CASE WHEN eee.excluded IS TRUE THEN NULL ELSE SUM(ee.emissions) END AS verified_emissions,
                               CASE WHEN eee.excluded IS TRUE THEN 'EXCLUDED' ELSE CAST(SUM(ee.emissions) AS VARCHAR) END AS verified_emissions_str
                        FROM account a
                        CROSS JOIN years
                        LEFT JOIN compliant_entity ce ON ce.id = a.compliant_entity_id
                        LEFT JOIN account_holder ah ON ah.id = a.account_holder_id
                        LEFT JOIN static_compliance_status scs ON scs.compliant_entity_id = ce.id AND scs.year = years.year
                        LEFT JOIN exclude_emissions_entry eee ON eee.compliant_entity_id = ce.identifier AND eee.year = years.year
                        LEFT JOIN (
                            SELECT *
                            FROM emissions_entry
                            WHERE (compliant_entity_id, upload_date) IN
                                  (SELECT compliant_entity_id, MAX(upload_date)
                                   FROM emissions_entry
                                   GROUP BY compliant_entity_id, year))
                        ee ON ee.compliant_entity_id = ce.identifier AND ee.year = years.year
                        WHERE a.account_status <> 'REJECTED'
                            AND a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT'
                            OR a.registry_account_type = 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT'
                            OR a.registry_account_type = 'MARITIME_OPERATOR_HOLDING_ACCOUNT'
                        GROUP BY years.year, a.id, ce.id, ah.id, scs.id, eee.id, years.id
                    ),
                    old_installation_accounts AS (
                        SELECT io.account_id
                        FROM installation_ownership io
                                 JOIN installation ins ON ins.compliant_entity_id = io.installation_id
                                 JOIN compliant_entity ce ON ce.id = io.installation_id
                                 JOIN account_data_per_year adpy on ce.identifier = adpy.identifier
                        UNION
                        SELECT acc.id
                        FROM aircraft_operator ao
                                 JOIN compliant_entity ce ON ce.id = ao.compliant_entity_id
                                 JOIN account acc ON acc.compliant_entity_id = ce.id
                                 JOIN account_data_per_year adpy on ce.identifier = adpy.identifier
                    )
                    
                    SELECT account_data_per_year.*,
                           COALESCE(surrenders, 0) - COALESCE(reversals, 0) AS surrendered_emissions,
                           surrender_transaction_pending,
                           receive_allowances_pending
                    FROM 
                        account_data_per_year
                        LEFT JOIN surrender_allowances ON surrender_allowances.year = account_data_per_year.year 
                        AND surrender_allowances.id = account_data_per_year.account_id
                        AND surrender_allowances.id IN (SELECT account_id FROM old_installation_accounts)
                        
                        LEFT JOIN reverse_allowances ON reverse_allowances.year = account_data_per_year.year 
                        AND reverse_allowances.id = account_data_per_year.account_id
                        AND reverse_allowances.id IN (SELECT account_id FROM old_installation_accounts)
                ) t1
            
                LEFT JOIN (
                    SELECT aa.account_id,
                           COUNT(*) AS total_ars,
                           STRING_AGG(DISTINCT uar.urid, ', ') AS total_ars_uris,
                           SUM(CASE WHEN aa.state = 'ACTIVE' AND uar.state = 'ENROLLED' THEN 1 ELSE 0 END) AS enrolled_ars,
                           SUM(CASE WHEN aa.state = 'ACTIVE' AND uar.state = 'VALIDATED' THEN 1 ELSE 0 END) AS validated_ars,
                           SUM(CASE WHEN aa.state = 'SUSPENDED' THEN 1 ELSE 0 END) AS suspended_ars
                    FROM account_access aa
                    JOIN users uar ON aa.user_id = uar.id
                    WHERE aa.access_right <> 'ROLE_BASED' and aa.state in ('ACTIVE', 'SUSPENDED')
                    GROUP BY aa.account_id
                ) ars ON ars.account_id = t1.account_id

                LEFT JOIN (
                    SELECT ac.id AS account_id, 
                        COUNT(*) AS ar_changes_count, 
                        STRING_AGG(ttt.request_identifier::TEXT, ',' ORDER BY ttt.request_identifier) AS ar_changes_ids,
                        STRING_AGG(
                            NULLIF(
                                CASE
                                    WHEN us.known_as <> '' THEN us.known_as
                                    ELSE CONCAT_WS(' ', NULLIF(us.first_name, ''), NULLIF(us.last_name, ''))
                                END, ''
                            ), ',' ORDER BY ttt.id
                        ) AS ar_changes_claimants
                    FROM task ttt
                    LEFT JOIN users us ON ttt.claimed_by = us.id
                    JOIN account ac ON ttt.account_id = ac.id
                    WHERE ttt.status = 'SUBMITTED_NOT_YET_APPROVED'
                    AND ttt.type IN ('AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST', 'AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST', 'AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST')
                    GROUP BY ac.id
                ) arc ON arc.account_id = t1.account_id
                
                LEFT JOIN LATERAL (
                    SELECT
                        ub.account_identifier as block_account_id,
                        SUM(CASE
                                WHEN ub.reserved_for_transaction IS NULL THEN ub.end_block - ub.start_block + 1
                                ELSE 0
                            END) AS totalAvailable,
                        SUM(CASE
                                WHEN ub.reserved_for_transaction IS NOT NULL THEN ub.end_block - ub.start_block + 1
                                ELSE 0
                            END) AS totalReserved
                    FROM unit_block ub
                    WHERE ub.account_identifier = t1.account_identifier
                    GROUP BY ub.account_identifier
                ) ub_summary ON ub_summary.block_account_id = t1.account_identifier
                
                GROUP BY regulator, ah_name, account_full_identifier, account_name, account_type, account_status, 
                         installation_name, identifier, permit_or_monitoring_plan_id, start_year, end_year, 
                         dynamic_compliance_status, account_holder_id, account_opening_date, 
                         account_closing_date, current_surrender_status, transactions_off_the_tal_allowed, 
                         ars.enrolled_ars, ars.validated_ars, ars.suspended_ars, ars.total_ars, ars.total_ars_uris, arc.ar_changes_count, arc.ar_changes_ids, 
                         arc.ar_changes_claimants, ub_summary.totalAvailable, ub_summary.totalReserved,
                         approval_second_ar_required, transfers_outside_tal, single_person_approval_required
                ORDER BY ah_name, permit_or_monitoring_plan_id DESC;
        """;

    @Override
    public List<ComplianceManagementReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public ComplianceManagementReportData mapRow(ResultSet rs, int i) throws SQLException {
        Long cumulativeEmissions = rs.getLong("cum_verified_emissions");
        Long cumulativeSurrenders = rs.getLong("cum_surrendered_emissions");
        return ComplianceManagementReportData.builder()
            .accountFullIdentifier(rs.getString("account_full_identifier"))
            .accountHolderId(rs.getLong("account_holder_id"))
            .operatorId(rs.getLong("identifier"))
            .permitOrMonitoringPlanId(rs.getString("permit_or_monitoring_plan_id"))
            .accountHolderName(rs.getString("ah_name"))
            .accountName(rs.getString("account_name"))
            .accountType(rs.getString("account_type"))
            .accountOpeningDate(formatLocalDateTime(parseDate(rs.getString("account_opening_date"))))
            .accountClosureDate(formatLocalDateTime(parseDate(rs.getString("account_closing_date"))))
            .accountStatus(rs.getString("account_status"))
            .regulator(rs.getString("regulator"))
            .firstYearOfVerifiedEmissions(rs.getString("start_year"))
            .lastYearOfVerifiedEmissions(rs.getString("end_year"))
            .surrenderBalance(cumulativeSurrenders - cumulativeEmissions)
            .currentSurrenderStatus(rs.getString("current_surrender_status"))
            .validatedARs(rs.getLong("validated_ars"))
            .enrolledARs(rs.getLong("enrolled_ars"))
            .suspendedARs(rs.getLong("suspended_ars"))
            .totalARs(rs.getLong("total_ars"))
            .arChangesInProgress(rs.getLong("ar_changes_count"))
            .arNominationTaskIds(rs.getString("ar_changes_ids"))
            .arNominationTaskClaimants(rs.getString("ar_changes_claimants"))
            .totalArsUris(rs.getString("total_ars_uris"))
            .totalAvailableQuantity(rs.getLong("totalAvailable"))
            .totalReservedQuantity(rs.getLong("totalReserved"))
            .surrenderTransactionPendingApproval(rs.getBoolean("surrender_transaction_pending"))
            .receiptOfAllowancesScheduled(rs.getBoolean("receive_allowances_pending"))
            .fourEyesTalTransactions(rs.getBoolean("approval_second_ar_required"))
            .transactionsOffTheTalAllowed(rs.getBoolean("transfers_outside_tal"))
            .fourEyesSurrenderReturnOfExcessAllocation(rs.getBoolean("single_person_approval_required"))
            .verifiedEmissions2021(rs.getString("verified_emissions_2021"))
            .verifiedEmissions2022(rs.getString("verified_emissions_2022"))
            .verifiedEmissions2023(rs.getString("verified_emissions_2023"))
            .verifiedEmissions2024(rs.getString("verified_emissions_2024"))
            .verifiedEmissions2025(rs.getString("verified_emissions_2025"))
            .verifiedEmissions2026(rs.getString("verified_emissions_2026"))
            .verifiedEmissions2027(rs.getString("verified_emissions_2027"))
            .verifiedEmissions2028(rs.getString("verified_emissions_2028"))
            .verifiedEmissions2029(rs.getString("verified_emissions_2029"))
            .verifiedEmissions2030(rs.getString("verified_emissions_2030"))
            .cumulativeEmissions(rs.getLong("cum_verified_emissions"))
            .cumulativeSurrenders(rs.getLong("cum_surrendered_emissions"))
            .staticSurrenderStatus2021(rs.getString("static_surrender_status_2021"))
            .staticSurrenderStatus2022(rs.getString("static_surrender_status_2022"))
            .staticSurrenderStatus2023(rs.getString("static_surrender_status_2023"))
            .staticSurrenderStatus2024(rs.getString("static_surrender_status_2024"))
            .staticSurrenderStatus2025(rs.getString("static_surrender_status_2025"))
            .staticSurrenderStatus2026(rs.getString("static_surrender_status_2026"))
            .staticSurrenderStatus2027(rs.getString("static_surrender_status_2027"))
            .staticSurrenderStatus2028(rs.getString("static_surrender_status_2028"))
            .staticSurrenderStatus2029(rs.getString("static_surrender_status_2029"))
            .staticSurrenderStatus2030(rs.getString("static_surrender_status_2030"))
            .build();
    }
}
