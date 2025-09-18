package gov.uk.ets.registry.api.tal.transaction.rules;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.commons.logging.RequestParamType;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanSubmitUpdateWhenAccountHasSpecificStatus;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanSubmitUpdateWhenUserStatusIsEnrolled;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanViewAccountWhenAccountAccessIsNotSuspended;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanViewAccountWhenAccountHasSpecificStatus;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanViewAccountWhenUserStatusIsEnrolled;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCannotSubmitRequestsForAccountsWithReadOnlyAccess;
import gov.uk.ets.registry.api.authz.ruleengine.features.AdminsWithAccountAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AuthoritiesWithAccountAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.CannotSubmitRequestWhenAccountIsTransferPendingStatusRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.RegistryAdministratorsCanSubmitRequestsForAnyEndUserAccount;
import gov.uk.ets.registry.api.authz.ruleengine.features.RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus;
import gov.uk.ets.registry.api.authz.ruleengine.features.RegistryAdministratorsCanViewAnyAccount;
import gov.uk.ets.registry.api.tal.service.TransactionRuleUpdateService;
import gov.uk.ets.registry.api.transaction.domain.data.TrustedAccountListRulesDTO;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles requests for fetching and updating the trusted account list transaction rules.
 */
@Tag(name = "Trusted Account List Transaction Rules Management")
@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class TalTransactionRulesController {

    private final TransactionRuleUpdateService transactionRuleUpdateService;

    public TalTransactionRulesController(TransactionRuleUpdateService transactionRuleUpdateService) {
        this.transactionRuleUpdateService = transactionRuleUpdateService;
    }

    /**
     * Gets the TAL transaction rules of the account.
     *
     * @param accountId the account id
     * @return the DTO of the TAL transaction rules
     */
    @Protected(
        {
            ARsCanViewAccountWhenAccountAccessIsNotSuspended.class,
            RegistryAdministratorsCanViewAnyAccount.class,
            ARsCanViewAccountWhenAccountAccessIsNotSuspended.class,
            ARsCanViewAccountWhenAccountHasSpecificStatus.class,
            ARsCanViewAccountWhenUserStatusIsEnrolled.class,
            AdminsWithAccountAccessRule.class,
            AuthoritiesWithAccountAccessRule.class
        }
    )
    @GetMapping(path = "tal-transaction-rules.get")
    public ResponseEntity<TrustedAccountListRulesDTO> getTalTransactionRulesByAccountId(
        @RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam @MDCParam(RequestParamType.ACCOUNT_ID) Long accountId) {
        return new ResponseEntity<>(transactionRuleUpdateService.createTrustedAccountListRulesDtoFromAccount(
            accountId), HttpStatus.OK);
    }

    /**
     * Updates the Tal transaction rules of the account.
     *
     * @param trustedAccountListRulesDTO the DTO of the TAL transaction rules
     * @param accountId                  the account id
     * @return the generated task business identifier
     */
    @Protected(
        {
            ARsCannotSubmitRequestsForAccountsWithReadOnlyAccess.class,
            RegistryAdministratorsCanSubmitRequestsForAnyEndUserAccount.class,
            ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended.class,
            ARsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
            ARsCanSubmitUpdateWhenUserStatusIsEnrolled.class,
            RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
            CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
            AdminsWithAccountAccessRule.class,
            AuthoritiesWithAccountAccessRule.class
        }
    )
    @PostMapping(path = "tal-transaction-rules.update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> updateTalTransactionRules(
        @RequestBody @Valid TrustedAccountListRulesDTO trustedAccountListRulesDTO,
        @RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam @MDCParam(RequestParamType.ACCOUNT_ID) Long accountId) {
        return new ResponseEntity<>(transactionRuleUpdateService.updateTalTransactionRules(
            trustedAccountListRulesDTO, accountId), HttpStatus.OK);
    }
}
