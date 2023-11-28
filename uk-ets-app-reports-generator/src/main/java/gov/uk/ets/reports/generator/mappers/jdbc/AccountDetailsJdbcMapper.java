package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.Util;
import gov.uk.ets.reports.generator.domain.Account;
import gov.uk.ets.reports.generator.domain.AccountDetailsReportData;
import gov.uk.ets.reports.generator.domain.AccountHolder;
import gov.uk.ets.reports.generator.domain.AccountHolderRepresentative;
import gov.uk.ets.reports.generator.domain.CompliantEntity;
import gov.uk.ets.reports.generator.domain.Contact;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AccountDetailsJdbcMapper
    implements ReportDataMapper<AccountDetailsReportData>, RowMapper<AccountDetailsReportData> {

    /**
     * The tricky part here is the derived table (see arsAggregated bellow). We use this trick to avoid the long
     * group by clause that would occur if we were aggregating the number of ARs directly in the select clause.
     */
    private static final String REPORT_QUERY =
            "select ce.regulator                                                            as regulator,\n" +
            "       a.full_identifier                                                       as account_number,\n" +
            "       a.account_name                                                          as account_name,\n" +
            "       a.type_label                                                            as account_type,\n" +
            "       a.account_status                                                        as account_status,\n" +
            "       a.opening_date                                                          as opening_date,\n" +
            "       a.closing_date                                                          as closing_date,\n" +
            "       a.compliance_status                                                     as compliance_status,\n" +
            "       a.balance                                                               as account_balance,\n" +
            "       a.excluded_from_billing                                                 as excluded_from_billing,\n" +
            "       a.excluded_from_billing_remarks                                         as excluded_from_billing_remarks,\n" +
            "       arsAggregated.numOfArs                                                  as number_of_ars,\n" +
            "       case when a.approval_second_ar_required = TRUE then 'YES' else 'NO' end as rule_approval_second_ar_required,\n" +
            "       case when a.transfers_outside_tal = TRUE then 'YES' else 'NO' end       as rule_transfers_outside_tal,\n" +
            "       case when a.single_person_approval_required = TRUE then 'YES' else 'NO' end as rule_single_person_approval_required,\n" +
            "       ah.identifier                                                           as ah_number,\n" +
            "       coalesce(ah.name, concat_ws(' ', ah.first_name, ah.last_name))          as ah_name,\n" +
            "       ah.registration_number                                                  as ah_registration_number,\n" +
            "       ah.no_reg_justification                                                 as no_reg_justification,\n" +
            "       ahContact.line_1                                                        as ah_address_line_1,\n" +
            "       ahContact.line_2                                                        as ah_address_line_2,\n" +
            "       ahContact.line_3                                                        as ah_address_line_3,\n" +
            "       ahContact.city                                                          as ah_city,\n" +
            "       ahContact.state_or_province                                             as ah_state_or_province,\n" +
            "       ahContact.country                                                       as ah_country,\n" +
            "       ahContact.post_code                                                     as ah_post_code,\n" +
            "       primaryAr.first_name                                                    as pc_first_name,\n" +
            "       primaryAr.last_name                                                     as pc_last_name,\n" +
            "       primaryContact.line_1                                                   as pc_address_line_1,\n" +
            "       primaryContact.line_2                                                   as pc_address_line_2,\n" +
            "       primaryContact.line_3                                                   as pc_address_line_3,\n" +
            "       primaryContact.city                                                     as pc_city,\n" +
            "       primaryContact.state_or_province                                        as pc_state_or_province,\n" +
            "       primaryContact.country                                                  as pc_country,\n" +
            "       case\n" +
            "           when primaryContact.phone_number_1_country is not null then\n" +
            "               concat_ws(' ', primaryContact.phone_number_1_country,\n" +
            "                         primaryContact.phone_number_1) end                    as pc_phone_1,\n" +
            "       case\n" +
            "           when primaryContact.phone_number_2_country is not null then\n" +
            "               concat_ws(' ', primaryContact.phone_number_2_country,\n" +
            "                         primaryContact.phone_number_2) end                    as pc_phone_2,\n" +
            "       primaryContact.email_address                                            as pc_email,\n" +
            "       altAr.first_name                                                        as apc_first_name,\n" +
            "       altAr.last_name                                                         as apc_last_name,\n" +
            "       altContact.line_1                                                       as apc_address_line_1,\n" +
            "       altContact.line_2                                                       as apc_address_line_2,\n" +
            "       altContact.line_3                                                       as apc_address_line_3,\n" +
            "       altContact.city                                                         as apc_city,\n" +
            "       altContact.state_or_province                                            as apc_state_or_province,\n" +
            "       altContact.country                                                      as apc_country,\n" +
            "       altContact.phone_number_1                                               as apc_phone_1,\n" +
            "       altContact.phone_number_2                                               as apc_phone_2,\n" +
            "       altContact.email_address                                                as apc_email,\n" +
            "       accountContact.line_1                                                   as billing_address_line_1,\n" +
            "       accountContact.line_2                                                   as billing_address_line_2,\n" +
            "       accountContact.line_3                                                   as billing_address_line_3,\n" +
            "       accountContact.post_code                                                as billing_address_postcode,\n" +
            "       accountContact.city                                                     as billing_address_city,\n" +
            "       accountContact.state_or_province                                         as billing_address_state_or_province,\n" +
            "       accountContact.country                                                  as billing_address_country,\n" +
            "       accountContact.contact_name                                             as billing_contact_name,\n" +
            "       case\n" +
            "           when accountContact.phone_number_1_country is not null then\n" +
            "               concat_ws(' ', accountContact.phone_number_1_country,\n" +
            "                         accountContact.phone_number_1) end                    as billing_phone_number,\n" +
            "       accountContact.email_address                                            as billing_email,\n" +
            "       accountContact.sop_customer_id                                          as sop_customer_id,\n" +
            "       case when i.compliant_entity_id IS NOT NULL then ce.identifier end      as installation_id,\n" +
            "       i.installation_name                                                     as installation_name,\n" +
            "       i.activity_type                                                         as installation_activity,\n" +
            "       i.permit_identifier                                                     as installation_permit_id,\n" +
            "       case when ao.compliant_entity_id IS NOT NULL then ce.identifier end     as aircraft_operator_id,\n" +
            "       ao.monitoring_plan_identifier                                           as monitoring_plan_id,\n" +
            "       ce.start_year                                                           as first_year_verified_emission_submission,\n" +
            "       ce.end_year                                                             as last_year_verified_emission_submission\n" +
            "from account a\n" +
            "         left join compliant_entity ce on a.compliant_entity_id = ce.id\n" +
            "         left join account_holder ah on ah.id = a.account_holder_id\n" +
            "         left join contact ahContact on ahContact.id = ah.contact_id\n" +
            "         left join installation i on ce.id = i.compliant_entity_id\n" +
            "         left join aircraft_operator ao on ce.id = ao.compliant_entity_id\n" +
            "         left join (select aa.account_id, count(aa.id) as numOfArs\n" +
            "                    from account_access aa\n" +
            "                    where aa.access_right <> 'ROLE_BASED'\n " +
            "                       and (aa.state = 'ACTIVE' or aa.state = 'SUSPENDED')\n" +
            "                    group by aa.account_id) as arsAggregated\n" +
            "                   on a.id = arsAggregated.account_id\n" +
            "         left join account_holder_representative primaryAr\n" +
            "                   on ah.id = primaryAr.account_holder_id and primaryAr.account_contact_type = 'PRIMARY'\n" +
            "         left join account_holder_representative altAr\n" +
            "                   on ah.id = altAr.account_holder_id and altAr.account_contact_type = 'ALTERNATIVE'\n" +
            "         left join contact primaryContact on primaryAr.contact_id = primaryContact.id\n" +
            "         left join contact altContact on altAr.contact_id = altContact.id\n" +
            "         left join contact accountContact on a.contact_id = accountContact.id\n" +
            "order by a.account_name, a.full_identifier;\n";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<AccountDetailsReportData> mapData(ReportCriteria criteria) {
        return null;
    }

    @Override
    public List<AccountDetailsReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public AccountDetailsReportData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return AccountDetailsReportData.builder()
            .account(Account.builder()
                .number(rs.getString("account_number"))
                .name(rs.getString("account_name"))
                .type(rs.getString("account_type"))
                .status(rs.getString("account_status"))
                .openingDate(LocalDateTime.parse(rs.getString("opening_date"), formatter))
                .closingDate(rs.getString("closing_date") != null ?
                    LocalDateTime.parse(rs.getString("closing_date"), formatter) : null)
                .complianceStatus(rs.getString("compliance_status"))
                .balance(rs.getLong("account_balance"))
                .numberOfARs(rs.getInt("number_of_ars"))
                .approvalSecondARRequired(rs.getString("rule_approval_second_ar_required"))
                .singlePersonApprovalRequired(rs.getString("rule_single_person_approval_required"))
                .transfersOutsideTal(rs.getString("rule_transfers_outside_tal"))
                .excludedFromBilling(rs.getBoolean("excluded_from_billing"))
                .excludedFromBillingRemarks(rs.getString("excluded_from_billing_remarks"))
                // billing
                .contact(Contact.builder()
                    .addressLine1(rs.getString("billing_address_line_1"))
                    .addressLine2(rs.getString("billing_address_line_2"))
                    .addressLine3(rs.getString("billing_address_line_3"))
                    .townOrCity(rs.getString("billing_address_city"))
                    .stateOrProvince(rs.getString("billing_address_state_or_province"))
                    .country(rs.getString("billing_address_country"))
                    .postCode(rs.getString("billing_address_postcode"))
                    .contactName(rs.getString("billing_contact_name"))
                    .phoneNumber1(rs.getString("billing_phone_number"))
                    .email(rs.getString("billing_email"))
                    .sopCustomerId(rs.getString("sop_customer_id"))
                    .build())
                .build())
            .accountHolder(AccountHolder.builder()
                .id(rs.getLong("ah_number"))
                .name(rs.getString("ah_name"))
                .companyRegistrationNumber(rs.getString("ah_registration_number"))
                .reasonForNoRegistrationNumber(rs.getString("no_reg_justification"))
                .contact(Contact.builder()
                    .addressLine1(rs.getString("ah_address_line_1"))
                    .addressLine2(rs.getString("ah_address_line_2"))
                    .addressLine3(rs.getString("ah_address_line_3"))
                    .townOrCity(rs.getString("ah_city"))
                    .stateOrProvince(rs.getString("ah_state_or_province"))
                    .country(rs.getString("ah_country"))
                    .postCode(rs.getString("ah_post_code"))
                    .build())
                .primaryAr(AccountHolderRepresentative.builder()
                    .firstName(rs.getString("pc_first_name"))
                    .lastName(rs.getString("pc_last_name"))
                    .contact(Contact.builder()
                        .addressLine1(rs.getString("pc_address_line_1"))
                        .addressLine2(rs.getString("pc_address_line_2"))
                        .addressLine3(rs.getString("pc_address_line_3"))
                        .townOrCity(rs.getString("pc_city"))
                        .stateOrProvince(rs.getString("pc_state_or_province"))
                        .country(rs.getString("pc_country"))
                        .phoneNumber1(rs.getString("pc_phone_1"))
                        .phoneNumber2(rs.getString("pc_phone_2"))
                        .email(rs.getString("pc_email"))
                        .build())
                    .build())
                .altAr(AccountHolderRepresentative.builder()
                    .firstName(rs.getString("apc_first_name"))
                    .lastName(rs.getString("apc_last_name"))
                    .contact(Contact.builder()
                        .addressLine1(rs.getString("apc_address_line_1"))
                        .addressLine2(rs.getString("apc_address_line_2"))
                        .addressLine3(rs.getString("apc_address_line_3"))
                        .townOrCity(rs.getString("apc_city"))
                        .stateOrProvince(rs.getString("apc_state_or_province"))
                        .country(rs.getString("apc_country"))
                        .phoneNumber1(rs.getString("apc_phone_1"))
                        .phoneNumber2(rs.getString("apc_phone_2"))
                        .email(rs.getString("apc_email"))
                        .build())
                    .build())
                .build())
            .compliantEntity(CompliantEntity.builder()
                .regulator(rs.getString("regulator"))
                .installationId(Util.getNullableLong(rs, "installation_id"))
                .installationName(rs.getString("installation_name"))
                .installationActivity(rs.getString("installation_activity"))
                .installationPermitId(rs.getString("installation_permit_id"))
                .aircraftOperatorId(Util.getNullableLong(rs, "aircraft_operator_id"))
                .monitoringPlanId(rs.getString("monitoring_plan_id"))
                .firstYearOfVerifiedEmissionSubmission(Util.getNullableLong(rs, "first_year_verified_emission_submission"))
                .lastYearOfVerifiedEmissionSubmission(Util.getNullableLong(rs, "last_year_verified_emission_submission"))
                .build())
            .build();
    }
}
