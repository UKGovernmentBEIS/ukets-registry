package gov.uk.ets.reports.generator.statistic;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class ComplianceStatisticDataReportService {

    private static final String UPSERT = """
        insert into compliance_records_summary (date,
        										oha_dss_error,
        										oha_dss_not_applicable,
        										oha_dss_exempt,
        										oha_dss_c,
        										oha_dss_b,
        										oha_dss_a,
        										total_oha,
        										aoha_dss_error,
        										aoha_dss_not_applicable,
        										aoha_dss_exempt,
        										aoha_dss_c,
        										aoha_dss_b,
        										aoha_dss_a,
        										total_aoha,
        										moha_dss_error,
        										moha_dss_not_applicable,
        										moha_dss_exempt,
        										moha_dss_c,
        										moha_dss_b,
        										moha_dss_a,
        										total_moha,
        										oha_cumulative_emissions,
        										oha_cumulative_surrenders,
        										oha_cumulative_reversals,
        										aoha_cumulative_emissions,
        										aoha_cumulative_surrenders,
        										aoha_cumulative_reversals,
        										moha_cumulative_emissions,
        										moha_cumulative_surrenders,
        										moha_cumulative_reversals,
        										total_cumulative_emissions,
        										total_cumulative_surrenders,
        										total_cumulative_reversals,
        										oha_exempt_most_recent_applicable,
        										aoha_exempt_most_recent_applicable,
        										moha_exempt_most_recent_applicable,
        										oha_live_most_recent_applicable,
        										aoha_live_most_recent_applicable,
        										moha_live_most_recent_applicable)
        (select operator_overview.date,
               operator_overview.oha_dss_error, operator_overview.oha_dss_not_applicable, operator_overview.oha_dss_exempt,
        	   operator_overview.oha_dss_c, operator_overview.oha_dss_b, operator_overview.oha_dss_a,
        	   operator_overview.total_oha,
        	   operator_overview.aoha_dss_error, operator_overview.aoha_dss_not_applicable, operator_overview.aoha_dss_exempt,
        	   operator_overview.aoha_dss_c, operator_overview.aoha_dss_b, operator_overview.aoha_dss_a,
        	   operator_overview.total_aoha,
        	   operator_overview.moha_dss_error, operator_overview.moha_dss_not_applicable, operator_overview.moha_dss_exempt,
        	   operator_overview.moha_dss_c, operator_overview.moha_dss_b, operator_overview.moha_dss_a,
        	   operator_overview.total_moha,
        	   coalesce(emissions.oha_cumulative_emissions, 0) as oha_cumulative_emissions,  
        	   coalesce(surrenders.oha_cumulative_surrenders, 0) as oha_cumulative_surrenders, 
        	   coalesce(reversals.oha_cumulative_reversals, 0) as oha_cumulative_reversals,
        	   coalesce(emissions.aoha_cumulative_emissions, 0) as aoha_cumulative_emissions, 
        	   coalesce(surrenders.aoha_cumulative_surrenders, 0) as aoha_cumulative_surrenders, 
        	   coalesce(reversals.aoha_cumulative_reversals, 0) as aoha_cumulative_reversals,
        	   coalesce(emissions.moha_cumulative_emissions, 0) as moha_cumulative_emissions, 
        	   coalesce(surrenders.moha_cumulative_surrenders, 0) as moha_cumulative_surrenders, 
        	   coalesce(reversals.moha_cumulative_reversals, 0) as moha_cumulative_reversals,
        	   coalesce(emissions.total_cumulative_emissions, 0) as total_cumulative_emissions, 
        	   coalesce(surrenders.total_cumulative_surrenders, 0) as total_cumulative_surrenders, 
        	   coalesce(reversals.total_cumulative_reversals, 0) as total_cumulative_reversals,
        	   operator_overview.oha_exempt_most_recent_applicable, operator_overview.aoha_exempt_most_recent_applicable,
        	   operator_overview.moha_exempt_most_recent_applicable, operator_overview.oha_live_most_recent_applicable,
        	   operator_overview.aoha_live_most_recent_applicable, operator_overview.moha_live_most_recent_applicable	  
        from (
        	select cast(NOW() as date) AS date,
        		   SUM(CASE WHEN a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'ERROR' THEN 1 ELSE 0 END) AS oha_dss_error,
        		   SUM(CASE WHEN a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'NOT_APPLICABLE' THEN 1 ELSE 0 END) AS oha_dss_not_applicable,
        		   SUM(CASE WHEN a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'EXCLUDED' THEN 1 ELSE 0 END) AS oha_dss_exempt,
        		   SUM(CASE WHEN a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'C' THEN 1 ELSE 0 END) AS oha_dss_c,
        		   SUM(CASE WHEN a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'B' THEN 1 ELSE 0 END) AS oha_dss_b,
        		   SUM(CASE WHEN a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'A' THEN 1 ELSE 0 END) AS oha_dss_a,
        		   SUM(CASE WHEN a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' THEN 1 ELSE 0 END) AS total_oha,
        		  
        		   SUM(CASE WHEN a.registry_account_type = 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'ERROR' THEN 1 ELSE 0 END) AS aoha_dss_error,
        		   SUM(CASE WHEN a.registry_account_type = 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'NOT_APPLICABLE' THEN 1 ELSE 0 END) AS aoha_dss_not_applicable,
        		   SUM(CASE WHEN a.registry_account_type = 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'EXCLUDED' THEN 1 ELSE 0 END) AS aoha_dss_exempt,
        		   SUM(CASE WHEN a.registry_account_type = 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'C' THEN 1 ELSE 0 END) AS aoha_dss_c,
        		   SUM(CASE WHEN a.registry_account_type = 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'B' THEN 1 ELSE 0 END) AS aoha_dss_b,
        		   SUM(CASE WHEN a.registry_account_type = 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'A' THEN 1 ELSE 0 END) AS aoha_dss_a,
        		   SUM(CASE WHEN a.registry_account_type = 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT' THEN 1 ELSE 0 END) AS total_aoha,
        		  
        		   SUM(CASE WHEN a.registry_account_type = 'MARITIME_OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'ERROR' THEN 1 ELSE 0 END) AS moha_dss_error,
        		   SUM(CASE WHEN a.registry_account_type = 'MARITIME_OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'NOT_APPLICABLE' THEN 1 ELSE 0 END) AS moha_dss_not_applicable,
        		   SUM(CASE WHEN a.registry_account_type = 'MARITIME_OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'EXCLUDED' THEN 1 ELSE 0 END) AS moha_dss_exempt,
        		   SUM(CASE WHEN a.registry_account_type = 'MARITIME_OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'C' THEN 1 ELSE 0 END) AS moha_dss_c,
        		   SUM(CASE WHEN a.registry_account_type = 'MARITIME_OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'B' THEN 1 ELSE 0 END) AS moha_dss_b,
        		   SUM(CASE WHEN a.registry_account_type = 'MARITIME_OPERATOR_HOLDING_ACCOUNT' AND a.compliance_status = 'A' THEN 1 ELSE 0 END) AS moha_dss_a,
        		   SUM(CASE WHEN a.registry_account_type = 'MARITIME_OPERATOR_HOLDING_ACCOUNT' THEN 1 ELSE 0 END) AS total_moha,
        		  
        		   SUM(CASE WHEN a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' AND eee.excluded = TRUE THEN 1 ELSE 0 END) AS oha_exempt_most_recent_applicable,
        		   SUM(CASE WHEN a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' AND (eee.excluded = FALSE OR eee.excluded is null) THEN 1 ELSE 0 END) AS oha_live_most_recent_applicable,
        		   SUM(CASE WHEN a.registry_account_type = 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT' AND eee.excluded = TRUE THEN 1 ELSE 0 END) AS aoha_exempt_most_recent_applicable,
        		   SUM(CASE WHEN a.registry_account_type = 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT' AND (eee.excluded = FALSE OR eee.excluded is null) THEN 1 ELSE 0 END) AS aoha_live_most_recent_applicable,
        		   SUM(CASE WHEN a.registry_account_type = 'MARITIME_OPERATOR_HOLDING_ACCOUNT' AND eee.excluded = TRUE THEN 1 ELSE 0 END) AS moha_exempt_most_recent_applicable,
        		   SUM(CASE WHEN a.registry_account_type = 'MARITIME_OPERATOR_HOLDING_ACCOUNT' AND (eee.excluded = FALSE OR eee.excluded is null) THEN 1 ELSE 0 END) AS moha_live_most_recent_applicable
        	  from account a
        	left join compliant_entity ce on ce.id = a.compliant_entity_id
        	left join exclude_emissions_entry eee on eee.compliant_entity_id = ce.identifier and eee.year = EXTRACT(YEAR FROM NOW())
        	 where a.registry_account_type in ('OPERATOR_HOLDING_ACCOUNT', 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT', 'MARITIME_OPERATOR_HOLDING_ACCOUNT')
        	   and a.account_status not in ('CLOSED', 'PROPOSED', 'REJECTED')) operator_overview
        join (
        	select cast(NOW() as date) AS date,
        		   SUM(CASE WHEN emi.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' THEN emi.emissions ELSE 0 END) AS oha_cumulative_emissions,
        		   SUM(CASE WHEN emi.registry_account_type = 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT' THEN emi.emissions ELSE 0 END) AS aoha_cumulative_emissions,
        		   SUM(CASE WHEN emi.registry_account_type = 'MARITIME_OPERATOR_HOLDING_ACCOUNT' THEN emi.emissions ELSE 0 END) AS moha_cumulative_emissions,
        		   SUM(emi.emissions) AS total_cumulative_emissions
        	from (
        		select a.registry_account_type, e.compliant_Entity_Id, e.year, e.emissions
        		  from account a
        		  join compliant_Entity c on a.compliant_Entity_Id = c.id
        		  join Emissions_Entry e on e.compliant_Entity_Id = c.identifier
        		 where a.registry_account_type in ('OPERATOR_HOLDING_ACCOUNT', 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT', 'MARITIME_OPERATOR_HOLDING_ACCOUNT')
        		 group by a.registry_account_type, e.compliant_Entity_Id,  e.year, e.upload_Date, e.emissions
        		having e.upload_Date = (select max(m.upload_Date) from Emissions_Entry m where e.compliant_Entity_Id=m.compliant_Entity_Id and e.year=m.year)
        		 order by a.registry_account_type, e.year) emi) emissions on emissions.date = operator_overview.date
        join (
            select cast(NOW() as date) AS date,
        		   SUM(CASE WHEN a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' THEN t.quantity ELSE 0 END) AS oha_cumulative_surrenders,
        		   SUM(CASE WHEN a.registry_account_type = 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT' THEN t.quantity ELSE 0 END) AS aoha_cumulative_surrenders,
        		   SUM(CASE WHEN a.registry_account_type = 'MARITIME_OPERATOR_HOLDING_ACCOUNT' THEN t.quantity ELSE 0 END) AS moha_cumulative_surrenders,
        		   SUM(t.quantity) AS total_cumulative_surrenders
              from transaction t
              join account a on t.transferring_account_identifier = a.identifier
            left join installation_ownership io on io.account_id = a.id
            left join installation ins on io.installation_id = ins.compliant_entity_id
             where a.registry_account_type in ('OPERATOR_HOLDING_ACCOUNT', 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT', 'MARITIME_OPERATOR_HOLDING_ACCOUNT')
               and t.type = 'SurrenderAllowances'
               and t.status = 'COMPLETED') surrenders on surrenders.date = emissions.date
        join (
        	select cast(NOW() as date) AS date,
        		   SUM(CASE WHEN a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' THEN t.quantity ELSE 0 END) AS oha_cumulative_reversals,
        		   SUM(CASE WHEN a.registry_account_type = 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT' THEN t.quantity ELSE 0 END) AS aoha_cumulative_reversals,
        		   SUM(CASE WHEN a.registry_account_type = 'MARITIME_OPERATOR_HOLDING_ACCOUNT' THEN t.quantity ELSE 0 END) AS moha_cumulative_reversals,
        		   SUM(t.quantity) AS total_cumulative_reversals
        	  from transaction t
              join account a on t.acquiring_account_identifier = a.identifier
        	left join installation_ownership io on io.account_id = a.id
        	left join installation ins on io.installation_id = ins.compliant_entity_id
        	 where a.registry_account_type in ('OPERATOR_HOLDING_ACCOUNT', 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT', 'MARITIME_OPERATOR_HOLDING_ACCOUNT')
        	   and t.type = 'ReverseSurrenderAllowances'
        	   and t.status = 'COMPLETED') reversals on reversals.date = surrenders.date
        ) ON CONFLICT (date) DO UPDATE
        SET oha_dss_error = EXCLUDED.oha_dss_error,
            oha_dss_not_applicable = EXCLUDED.oha_dss_not_applicable,
            oha_dss_exempt = EXCLUDED.oha_dss_exempt,
            oha_dss_c = EXCLUDED.oha_dss_c,
            oha_dss_b = EXCLUDED.oha_dss_b,
            oha_dss_a = EXCLUDED.oha_dss_a,
            total_oha = EXCLUDED.total_oha,
            aoha_dss_error = EXCLUDED.aoha_dss_error,
            aoha_dss_not_applicable = EXCLUDED.aoha_dss_not_applicable,
            aoha_dss_exempt = EXCLUDED.aoha_dss_exempt,
            aoha_dss_c = EXCLUDED.aoha_dss_c,
            aoha_dss_b = EXCLUDED.aoha_dss_b,
            aoha_dss_a = EXCLUDED.aoha_dss_a,
            total_aoha = EXCLUDED.total_aoha,
            moha_dss_error = EXCLUDED.moha_dss_error,
            moha_dss_not_applicable = EXCLUDED.moha_dss_not_applicable,
            moha_dss_exempt = EXCLUDED.moha_dss_exempt,
            moha_dss_c = EXCLUDED.moha_dss_c,
            moha_dss_b = EXCLUDED.moha_dss_b,
            moha_dss_a = EXCLUDED.moha_dss_a,
            total_moha = EXCLUDED.total_moha,
            oha_cumulative_emissions = EXCLUDED.oha_cumulative_emissions,
            oha_cumulative_surrenders = EXCLUDED.oha_cumulative_surrenders,
            oha_cumulative_reversals = EXCLUDED.oha_cumulative_reversals,
            aoha_cumulative_emissions = EXCLUDED.aoha_cumulative_emissions,
            aoha_cumulative_surrenders = EXCLUDED.aoha_cumulative_surrenders,
            aoha_cumulative_reversals = EXCLUDED.aoha_cumulative_reversals,
            moha_cumulative_emissions = EXCLUDED.moha_cumulative_emissions,
            moha_cumulative_surrenders = EXCLUDED.moha_cumulative_surrenders,
            moha_cumulative_reversals = EXCLUDED.moha_cumulative_reversals,
            total_cumulative_emissions = EXCLUDED.total_cumulative_emissions,
            total_cumulative_surrenders = EXCLUDED.total_cumulative_surrenders,
            total_cumulative_reversals = EXCLUDED.total_cumulative_reversals,
            oha_exempt_most_recent_applicable = EXCLUDED.oha_exempt_most_recent_applicable,
            aoha_exempt_most_recent_applicable = EXCLUDED.aoha_exempt_most_recent_applicable,
            moha_exempt_most_recent_applicable = EXCLUDED.moha_exempt_most_recent_applicable,
            oha_live_most_recent_applicable = EXCLUDED.oha_live_most_recent_applicable,
            aoha_live_most_recent_applicable = EXCLUDED.aoha_live_most_recent_applicable,
            moha_live_most_recent_applicable = EXCLUDED.moha_live_most_recent_applicable;
        """;

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void calculateSnapshot() {
        log.info("Calculating Compliance statistics at: {}", new Date());
        jdbcTemplate.execute(UPSERT);
    }
}
