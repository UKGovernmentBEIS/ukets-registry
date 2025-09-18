package gov.uk.ets.registry.api.compliance;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.authz.ruleengine.features.AccountIsNotClosedOrClosurePendingStatusRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AdminsWithAccountAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AnyAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AuthoritiesWithAccountAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.CannotSubmitRequestWhenAccountIsTransferPendingStatusRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorAdminRule;
import gov.uk.ets.registry.api.compliance.service.ComplianceService;
import gov.uk.ets.registry.api.compliance.web.model.ComplianceOverviewDTO;
import gov.uk.ets.registry.api.compliance.web.model.ComplianceStatusHistoryResultDTO;
import gov.uk.ets.registry.api.compliance.web.model.OperatorEmissionsExclusionStatusChangeDTO;
import gov.uk.ets.registry.api.compliance.web.model.VerifiedEmissionsResultDTO;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static gov.uk.ets.commons.logging.RequestParamType.ACCOUNT_ID;
import static gov.uk.ets.commons.logging.RequestParamType.COMPLIANT_ENTITY_ID;

@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@AllArgsConstructor
public class ComplianceController {

    private final ComplianceService complianceService;


    /**
     * Updates the operator's exclusion status for a specific year.
     *
     * @param accountIdentifier the account identifier
     * @param patch             the AccountStatusChangeDTO patch
     */
    @Protected({
        SeniorAdminRule.class,
        CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
        AccountIsNotClosedOrClosurePendingStatusRule.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    @PatchMapping(path = "compliance.update.exclusion-status", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updateAccountExclusionStatus(@RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam @MDCParam(ACCOUNT_ID) Long accountIdentifier,
                                             @Valid @RequestBody OperatorEmissionsExclusionStatusChangeDTO patch) {
        complianceService.updateExclusionStatus(accountIdentifier, patch);
    }


    /**
     * Returns the account holdings information for the account with the given business identifier.
     *
     * @param accountIdentifier the account business identifier.
     * @return an account
     */
    @Protected({
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    @GetMapping(path = "compliance.get.overview")
    @ResponseStatus(HttpStatus.OK)
    public ComplianceOverviewDTO getComplianceOverview(
        @NotNull @RequestParam @RuleInput(RuleInputType.ACCOUNT_ID) @MDCParam(ACCOUNT_ID) Long accountIdentifier) {

        return complianceService.getComplianceOverview(accountIdentifier);
    }

    /**
     * Gets the verified emissions for the provided compliant entity id.
     *
     * @param compliantEntityId the compliant entity identifier
     */
    @GetMapping(path = "compliance.get.emissions")
    @ResponseStatus(HttpStatus.OK)
    public VerifiedEmissionsResultDTO getReportableVerifiedEmissions(@RequestParam @MDCParam(COMPLIANT_ENTITY_ID) Long compliantEntityId) {
        int currentYear = LocalDate.now().getYear();
        return complianceService.getReportableVerifiedEmissions(compliantEntityId, currentYear);
    }

    /**
     * Gets the verified emissions for the provided compliant entity id.
     *
     * @param compliantEntityId the compliant entity identifier
     */
    @GetMapping(path = "compliance.get.status.history")
    @ResponseStatus(HttpStatus.OK)
    public ComplianceStatusHistoryResultDTO getComplianceStatusHistory(@RequestParam @MDCParam(COMPLIANT_ENTITY_ID) Long compliantEntityId) {
        return complianceService.getComplianceStatusHistory(compliantEntityId);
    }

    /**
     * Gets the verified emissions for the provided compliant entity id.
     *
     * @param compliantEntityId the compliant entity identifier
     */
    @Protected({
        AnyAdminRule.class,
    })
    @GetMapping(path = "compliance.get.events.history")
    @ResponseStatus(HttpStatus.OK)
    public List<AuditEventDTO> getComplianceEventsHistory(@RequestParam @MDCParam(COMPLIANT_ENTITY_ID) Long compliantEntityId) {
        return complianceService.getComplianceEventsHistory(compliantEntityId);
    }
    

    /**
     * Triggers compliance status recalculation events for all compliant entities.
     */
    @Protected({
        AnyAdminRule.class,
    })
    @PostMapping(path = "compliance.recalculate.dynamic-status")
    @ResponseStatus(HttpStatus.CREATED)
    public void recalculateDynamicStatusAllCompliantEntities() {
        complianceService.recalculateDynamicStatus();
    }
}
