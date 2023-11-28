package gov.uk.ets.registry.api.accountholder;

import static gov.uk.ets.commons.logging.RequestParamType.ACCOUNT_HOLDER_ID;
import static gov.uk.ets.commons.logging.RequestParamType.ACCOUNT_ID;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderContactInfoDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderFileDTO;
import gov.uk.ets.registry.api.accountholder.service.AccountHolderService;
import gov.uk.ets.registry.api.accountholder.service.AccountHolderUpdateService;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderContactUpdateDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderDetailsUpdateDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderTypeAheadSearchResultDTO;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanSubmitUpdateWhenAccountHasSpecificStatus;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanSubmitUpdateWhenUserStatusIsEnrolled;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanViewAccountWhenAccountAccessIsNotSuspended;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanViewAccountWhenAccountHasSpecificStatus;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanViewAccountWhenUserStatusIsEnrolled;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanViewRequestsOnlyForAccountsWithAccess;
import gov.uk.ets.registry.api.authz.ruleengine.features.AdminsWithAccountAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AffectedUserCannotPerformActionRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AuthoritiesWithAccountAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.CannotSubmitRequestWhenAccountIsTransferPendingStatusRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.ReadOnlyAdministratorsCannotSubmitRequest;
import gov.uk.ets.registry.api.authz.ruleengine.features.RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorOrJuniorAdministratorRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.holder.rules.CannotApplyDuplicateAccountHolderUpdateAlternativePrimaryContactDetailsOnAccount;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.holder.rules.CannotApplyDuplicateAccountHolderUpdateDetailsOnAccount;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.holder.rules.CannotApplyDuplicateAccountHolderUpdatePrimaryContactDetailsOnAccount;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@AllArgsConstructor
public class AccountHolderController {

    private AccountHolderService accountHolderService;
    private AccountHolderUpdateService accountHolderUpdateService;

    /**
     * Retrieves an account holder based on identifier (TypeAhead component requires this API endpoint) .
     *
     * @param identifier The account holder business identifier.
     * @return some account holders.
     */
    @GetMapping(path = "account-holder.get.by-identifier", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AccountHolderTypeAheadSearchResultDTO>> getAccountHoldersByIdentifier(
            @RequestParam @MDCParam(ACCOUNT_HOLDER_ID) String identifier, @RequestParam Set<AccountHolderType> types) {

        ResponseEntity<List<AccountHolderTypeAheadSearchResultDTO>> result = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        List<AccountHolderTypeAheadSearchResultDTO> holders =
            accountHolderService.getAccountHolders(identifier, types);
        if (holders != null) {
            result = new ResponseEntity<>(holders, HttpStatus.OK);
        }
        return result;
    }

    /**
     * Retrieves account holders based on their name.
     *
     * @param name The holder name.
     * @param type The holder type (e.g. organisation)
     * @return some account holders.
     */
    @GetMapping(path = "account-holder.get.by-name", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AccountHolderTypeAheadSearchResultDTO>> getAccountHolders(@RequestParam String name,
                                                                                         @RequestParam
                                                                                             AccountHolderType type) {
        ResponseEntity<List<AccountHolderTypeAheadSearchResultDTO>> result;
        List<AccountHolderTypeAheadSearchResultDTO> holders =
            accountHolderService.getAccountHolders(name.toUpperCase(), type);
        if (holders == null) {
            holders = new ArrayList<>();
        }
        result = new ResponseEntity<>(holders, HttpStatus.OK);
        return result;
    }

    /**
     * Creates a task for the Account holder details update.
     *
     * @param body              The account holder update action
     * @param accountIdentifier The account identifier
     * @return The task request id
     */
    @Protected({
        ReadOnlyAdministratorsCannotSubmitRequest.class,
        RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
        ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended.class,
        ARsCanSubmitUpdateWhenUserStatusIsEnrolled.class,
        ARsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
        CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
        CannotApplyDuplicateAccountHolderUpdateDetailsOnAccount.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    @PostMapping(path = "account-holder.update-details", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> updateAccountHolderDetails(@RequestBody @Valid AccountHolderDetailsUpdateDTO body,
                                                           @RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam
                                                                   Long accountIdentifier,
                                                           @RuleInput(RuleInputType.ACCOUNT_HOLDER_ID) @RequestParam
                                                           @MDCParam(ACCOUNT_HOLDER_ID) Long accountHolderIdentifier) {
        Long taskRequestId =
            accountHolderUpdateService.submitAccountHolderDetailsUpdateRequest(body, accountIdentifier);
        return new ResponseEntity<>(taskRequestId, HttpStatus.OK);
    }

    /**
     * Creates a task for the Account holder primary contact details update.
     *
     * @param body              The account holder update action
     * @param accountIdentifier The account identifier
     * @return The task request id
     */
    @Protected({
        ReadOnlyAdministratorsCannotSubmitRequest.class,
        RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
        ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended.class,
        ARsCanSubmitUpdateWhenUserStatusIsEnrolled.class,
        ARsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
        CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
        CannotApplyDuplicateAccountHolderUpdatePrimaryContactDetailsOnAccount.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    @PostMapping(path = "account-holder.update-primary-contact", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> updateAccountHolderPrimaryContact(
        @RequestBody @Valid AccountHolderContactUpdateDTO body,
        @RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam Long accountIdentifier,
        @RuleInput(RuleInputType.ACCOUNT_HOLDER_ID) @RequestParam
        @MDCParam(ACCOUNT_HOLDER_ID) Long accountHolderIdentifier) {
        Long taskRequestId =
            accountHolderUpdateService.submitAccountHolderContactUpdateRequest(body, accountIdentifier);
        return new ResponseEntity<>(taskRequestId, HttpStatus.OK);
    }

    /**
     * Creates a task for the Account holder alternative primary contact details update.
     *
     * @param body
     * @param accountIdentifier
     * @param accountHolderIdentifier
     * @return
     */
    @Protected({
        ReadOnlyAdministratorsCannotSubmitRequest.class,
        RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
        ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended.class,
        ARsCanSubmitUpdateWhenUserStatusIsEnrolled.class,
        ARsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
        CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
        CannotApplyDuplicateAccountHolderUpdateAlternativePrimaryContactDetailsOnAccount.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    @PostMapping(path = "account-holder.update-alternative-primary-contact", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> updateAccountHolderAlternativePrimaryContact(
        @RequestBody @Valid AccountHolderContactUpdateDTO body,
        @RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam Long accountIdentifier,
        @RuleInput(RuleInputType.ACCOUNT_HOLDER_ID) @RequestParam
        @MDCParam(ACCOUNT_HOLDER_ID) Long accountHolderIdentifier) {
        Long taskRequestId =
            accountHolderUpdateService.submitAccountHolderContactUpdateRequest(body, accountIdentifier);
        return new ResponseEntity<>(taskRequestId, HttpStatus.OK);
    }

    /**
     * Retrieves account holders for the current user.
     *
     * @param holderType The holder type (e.g. organisation)
     * @return some account holders.
     */
    @GetMapping(path = "account-holder.get.list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AccountHolderDTO>> getAccountHoldersFromAuthorisedRepresentative(
        @RequestParam AccountHolderType holderType) {
        List<AccountHolderDTO> holders = accountHolderService.getAccountHolders(holderType, AccountAccessState.ACTIVE);
        return ResponseEntity.ok(holders);
    }

    /**
     * Retrieves an account holder based on .
     *
     * @param identifier The account holder business identifier.
     * @return some account holders.
     */
    @GetMapping(path = "account-holder.get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountHolderDTO> getAccountHolder(@RequestParam @MDCParam(ACCOUNT_HOLDER_ID)
                                                                         Long identifier) {

        ResponseEntity<AccountHolderDTO> result = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        AccountHolderDTO holder = accountHolderService.getAccountHolder(identifier);
        if (holder != null) {
            result = new ResponseEntity<>(holder, HttpStatus.OK);
        }
        return result;
    }

    /**
     * Retrieves the account holder contact info, primary and alternative contacts.
     *
     * @param identifier The account holder identifier
     * @return The {@link AccountHolderContactInfoDTO} of the account holder
     */
    @GetMapping(path = "account-holder.get.contacts")
    public AccountHolderContactInfoDTO getAccountHolderContactInfo(@RequestParam("holderId")
                                                                   @MDCParam(ACCOUNT_HOLDER_ID) Long identifier) {
        return accountHolderService.getAccountHolderContactInfo(identifier);
    }

    /**
     * Returns a list of Account Holder Files
     *
     * @param accountIdentifier The identifier of the account
     * @return A list of files
     */
    @Protected({
        ARsCanViewRequestsOnlyForAccountsWithAccess.class,
        ARsCanViewAccountWhenUserStatusIsEnrolled.class,
        ARsCanViewAccountWhenAccountHasSpecificStatus.class,
        ARsCanViewAccountWhenAccountAccessIsNotSuspended.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    @GetMapping(path = "account-holder.get.files", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AccountHolderFileDTO>> getAccountHolderFiles(
        @RequestParam @RuleInput(RuleInputType.ACCOUNT_ID) @MDCParam(ACCOUNT_ID) Long accountIdentifier) {
        return new ResponseEntity<>(accountHolderService.getAccountHolderFiles(accountIdentifier), HttpStatus.OK);
    }

    /**
     * Deletes both accountHolderFile and uploadedFile.
     *
     * @param id     The account holder identifier
     * @param fileId The file identifier
     */
    @Protected({
        SeniorOrJuniorAdministratorRule.class,
        AffectedUserCannotPerformActionRule.class
    })
    @DeleteMapping(path = "account-holder.delete.file")
    public ResponseEntity<Void> deleteAccountHolderFile(@RequestParam @MDCParam(ACCOUNT_HOLDER_ID) Long id,
                                                        @RequestParam Long fileId) {
        accountHolderService.deleteAccountHolderFile(id, fileId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Download account holder file by id
     *
     * @param fileId The id of the file
     * @return The file
     */
    @GetMapping(path = "account-holder.get.file", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getAccountHolderFile(@RequestParam Long fileId) {
        UploadedFile file = accountHolderService.getFileById(fileId);
        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION);
        headers.add(CONTENT_DISPOSITION,
            ContentDisposition.builder("attachment").filename(file.getFileName())
                .build().toString());
        return new ResponseEntity<>(file.getFileData(), headers, HttpStatus.OK);
    }
}
