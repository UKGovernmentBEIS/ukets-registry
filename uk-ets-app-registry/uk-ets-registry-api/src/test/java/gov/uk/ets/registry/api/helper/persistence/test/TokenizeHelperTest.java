package gov.uk.ets.registry.api.helper.persistence.test;


import static gov.uk.ets.registry.api.helper.persistence.TokenizeHelper.tokenizeClause;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import static gov.uk.ets.registry.api.helper.persistence.TokenizeHelper.extractSelectClause;

class TokenizeHelperTest {

    private static class QuerySelectClauseStringsArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                Arguments.of(" select task0_.request_identifier as col_0_0_, task0_.type as col_1_0_, " +
                        "user1_.first_name as col_2_0_, user1_.last_name as col_3_0_, task0_.initiated_by as col_4_0_, " +
                        "user1_.urid as col_5_0_, user5_.first_name as col_6_0_, user5_.last_name as col_7_0_, " +
                        "account2_.identifier as col_8_0_, account2_.full_identifier as col_9_0_, " +
                        "account2_.kyoto_account_type as col_10_0_, account2_.registry_account_type as col_11_0_, " +
                        "account2_.account_status as col_12_0_, accounthol4_.name as col_13_0_, " +
                        "user3_.disclosed_name as col_14_0_, case when user3_.disclosed_name='Registry Administrator' then null else user3_.urid end as col_15_0_, " +
                        "task0_.transaction_identifier as col_16_0_, task0_.initiated_date as col_17_0_, " +
                        "task0_.status as col_18_0_, task0_.recipient_account_number as col_19_0_, " +
                        "task0_.difference as col_20_0_, user5_.urid as col_21_0_, " +
                        "task0_.completed_date as col_22_0_, accounthol4_.identifier as col_23_0_, " +
                        "account2_.type_label as col_24_0_ from task task0_ inner join users user1_ " +
                        "on task0_.initiated_by=user1_.id left outer join account account2_ on task0_.account_id=account2_.id " +
                        "left outer join users user3_ on task0_.user_id=user3_.id left outer join account_holder accounthol4_ " +
                        "on account2_.account_holder_id=accounthol4_.id left outer join users user5_ on task0_.claimed_by=user5_.id " +
                        "where (task0_.id is not null) and " +
                        "task0_.status<>'APPROVED' and task0_.status<>'REJECTED' and " +
                        "(task0_.type not in  ('ADD_TRUSTED_ACCOUNT_REQUEST' , 'DELETE_TRUSTED_ACCOUNT_REQUEST' , 'TRANSACTION_REQUEST' , 'AR_REQUESTED_DOCUMENT_UPLOAD')) and " +
                        "(task0_.claimed_by in (select user17_.id from users user17_ inner join user_role_mapping userroles18_ on user17_.id=userroles18_.user_id " +
                        "inner join iam_user_role iamuserrol19_ on userroles18_.role_id=iamuserrol19_.id " +
                        "where iamuserrol19_.role_name in ('verifier' , 'junior-registry-administrator' , 'readonly-administrator' , 'senior-registry-administrator' , 'system-administrator' , 'authority-user')) " +
                        "or task0_.claimed_by is null)",
                    Arrays.asList("request_identifier",
                        "type",
                        "first_name",
                        "last_name",
                        "initiated_by",
                        "urid",
                        "first_name",
                        "last_name",
                        "identifier",
                        "full_identifier",
                        "kyoto_account_type",
                        "registry_account_type",
                        "account_status",
                        "name",
                        "disclosed_name",
                        "disclosed_name='Registry",
                        "urid",
                        "transaction_identifier",
                        "initiated_date",
                        "status",
                        "recipient_account_number",
                        "difference",
                        "urid",
                        "completed_date",
                        "identifier",
                        "type_label")),
                Arguments.of(" select task0_.request_identifier as col_0_0_, " +
                        "task0_.type as col_1_0_, user1_.first_name as col_2_0_, " +
                        "user1_.last_name as col_3_0_, task0_.initiated_by as col_4_0_, " +
                        "user1_.urid as col_5_0_, user5_.first_name as col_6_0_, " +
                        "user5_.last_name as col_7_0_, account2_.identifier as col_8_0_, " +
                        "account2_.full_identifier as col_9_0_, account2_.kyoto_account_type as col_10_0_, " +
                        "account2_.registry_account_type as col_11_0_, account2_.account_status as col_12_0_, " +
                        "accounthol4_.name as col_13_0_, user3_.disclosed_name as col_14_0_, " +
                        "case when user3_.disclosed_name='Registry Administrator' then null else user3_.urid end as col_15_0_, " +
                        "task0_.transaction_identifier as col_16_0_, task0_.initiated_date as col_17_0_, " +
                        "task0_.status as col_18_0_, task0_.recipient_account_number as col_19_0_, " +
                        "task0_.difference as col_20_0_, user5_.urid as col_21_0_, task0_.completed_date as col_22_0_, " +
                        "accounthol4_.identifier as col_23_0_, account2_.type_label as col_24_0_ " +
                        "from task task0_ inner join users user1_ on task0_.initiated_by=user1_.id " +
                        "left outer join account account2_ on task0_.account_id=account2_.id left outer join users user3_ " +
                        "on task0_.user_id=user3_.id left outer join account_holder accounthol4_ on account2_.account_holder_id=accounthol4_.id " +
                        "left outer join users user5_ on task0_.claimed_by=user5_.id where (task0_.id is not null) and " +
                        "task0_.status<>'APPROVED' and task0_.status<>'REJECTED'",
                    Arrays.asList("request_identifier",
                        "type",
                        "first_name",
                        "last_name",
                        "initiated_by",
                        "urid",
                        "first_name",
                        "last_name",
                        "identifier",
                        "full_identifier",
                        "kyoto_account_type",
                        "registry_account_type",
                        "account_status",
                        "name",
                        "disclosed_name",
                        "disclosed_name='Registry",
                        "urid",
                        "transaction_identifier",
                        "initiated_date",
                        "status",
                        "recipient_account_number",
                        "difference",
                        "urid",
                        "completed_date",
                        "identifier",
                        "type_label")),
                Arguments.of("select searchableTransaction.identifier, searchableTransaction.type, " +
                        "searchableTransaction.status, searchableTransaction.lastUpdated, " +
                        "searchableTransaction.quantity, searchableTransaction.unitType, " +
                        "searchableTransaction.transferringAccount.accountFullIdentifier, " +
                        "transferring.accountName, searchableTransaction.transferringAccount.accountType, " +
                        "transferring.registryAccountType, transferring.identifier, transferring.accountType, " +
                        "searchableTransaction.acquiringAccount.accountFullIdentifier, acquiring.accountName, " +
                        "searchableTransaction.acquiringAccount.accountType, acquiring.registryAccountType, " +
                        "acquiring.identifier, acquiring.accountType, searchableTransaction.started " +
                        "from SearchableTransaction searchableTransaction " +
                        "  left join searchableTransaction.transferringUkRegistryAccount as transferring " +
                        "  left join searchableTransaction.acquiringUkRegistryAccount as acquiring " +
                        "where searchableTransaction is not null and lower(searchableTransaction.identifier) like ?1 escape '!'",
                    Arrays.asList("identifier",
                        "type",
                        "status",
                        "lastUpdated",
                        "quantity",
                        "unitType",
                        "transferringAccount.accountFullIdentifier",
                        "accountName",
                        "transferringAccount.accountType",
                        "registryAccountType",
                        "identifier",
                        "accountType",
                        "acquiringAccount.accountFullIdentifier",
                        "accountName",
                        "acquiringAccount.accountType",
                        "registryAccountType",
                        "identifier",
                        "accountType",
                        "started")),
                Arguments.of("select account0_.id as id1_1_, account0_.account_holder_id as account20_1_, " +
                        "account0_.account_name as account_2_1_, account0_.account_status as account_3_1_, " +
                        "account0_.type_label as type_lab4_1_, account0_.approval_second_ar_required as approval5_1_, " +
                        "account0_.balance as balance6_1_, account0_.billing_address_same_as_account_holder_address as billing_7_1_, " +
                        "account0_.check_digits as check_di8_1_, account0_.commitment_period_code as commitme9_1_, " +
                        "account0_.compliance_status as complia10_1_, account0_.compliant_entity_id as complia21_1_, " +
                        "account0_.contact_id as contact22_1_, account0_.full_identifier as full_id11_1_, account0_.identifier as identif12_1_, " +
                        "account0_.kyoto_account_type as kyoto_a13_1_, account0_.opening_date as opening14_1_, account0_.registry_account_type as registr15_1_, " +
                        "account0_.registry_code as registr16_1_, account0_.request_status as request17_1_, account0_.transfers_outside_tal as transfe18_1_, " +
                        "account0_.unit_type as unit_ty19_1_ from account account0_ inner join account_holder accounthol1_ on account0_.account_holder_id=accounthol1_.id " +
                        "and (lower(trim(accounthol1_.name)) like '%test%' or lower((trim(accounthol1_.first_name)||trim(accounthol1_.last_name))) like '%test%' escape '!' " +
                        "or lower((trim(accounthol1_.last_name)||trim(accounthol1_.first_name))) like '%test%' escape '!') inner join  (compliant_entity compliante7_ " +
                        "left outer join aircraft_operator compliante7_1_ on compliante7_.id=compliante7_1_.compliant_entity_id left outer join installation compliante7_2_ " +
                        "on compliante7_.id=compliante7_2_.compliant_entity_id) on account0_.compliant_entity_id=compliante7_.id and ((account0_.id is not null) and " +
                        "compliante7_.regulator='EA' and (lower(compliante7_2_.permit_identifier) like '%123%' escape '!' or lower(compliante7_1_.monitoring_plan_identifier) like '%123%' escape '!') and " +
                        "compliante7_.allocation_classification='NOT_YET_ALLOCATED' and (cast(compliante7_.identifier as varchar) like '%1000045%' escape '!')) " +
                        "inner join account_access accountacc8_ on account0_.id=accountacc8_.account_id inner join users user9_ on accountacc8_.user_id=user9_.id and " +
                        "(lower(user9_.urid) like '%uk405681794859%' escape '!') where (account0_.id is not null) and (lower(account0_.full_identifier) like '%uk-100-10000050-0-5%' " +
                        "or lower(account0_.account_name) like '%uk-100-10000050-0-5%') and account0_.account_status='OPEN' and account0_.account_status<>'REJECTED' and " +
                        "(account0_.id is null or account0_.registry_account_type='AIRCRAFT_OPERATOR_HOLDING_ACCOUNT' and account0_.kyoto_account_type='PARTY_HOLDING_ACCOUNT') and " +
                        "account0_.compliance_status='COMPLIANT'",
                    Arrays.asList("id",
                        "account_holder_id",
                        "account_name",
                        "account_status",
                        "type_label",
                        "approval_second_ar_required",
                        "balance",
                        "billing_address_same_as_account_holder_address",
                        "check_digits",
                        "commitment_period_code",
                        "compliance_status",
                        "compliant_entity_id",
                        "contact_id",
                        "full_identifier",
                        "identifier",
                        "kyoto_account_type",
                        "opening_date",
                        "registry_account_type",
                        "registry_code",
                        "request_status",
                        "transfers_outside_tal",
                        "unit_type"))
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(QuerySelectClauseStringsArgumentsProvider.class)
    void checkThatQueryTokensFromQueryClauseAreReturnedCorrectly(String input, List<String> expected) {
        assertThat(tokenizeClause(extractSelectClause(input))).containsExactlyElementsOf((expected));
    }
}
