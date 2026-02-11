package gov.uk.ets.registry.api.accountholder.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.service.AccountClaimService;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderContactInfoDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactSendInvitationDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderDetailsUpdateDiffDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderInAccountsDTO;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.OnlySeniorOrJuniorCanClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.SeniorAdminCanClaimTaskInitiatedByAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.*;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.file.upload.requesteddocs.service.RequestedDocsTaskService;
import gov.uk.ets.registry.api.integration.changelog.service.AccountAuditService;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.AccountHolderUpdateTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class AccountHolderUpdateTaskService implements TaskTypeService<AccountHolderUpdateTaskDetailsDTO> {

    private final AccountService accountService;
    private final AccountHolderService accountHolderService;
    private final RequestedDocsTaskService requestedDocsTaskService;
    private final Mapper mapper;

    private final AccountAuditService accountAuditService;
    private final AccountClaimService accountClaimService;

    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS,
                      RequestType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS,
                      RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE,
                      RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE,
                      RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD);
    }

    @Override
    public AccountHolderUpdateTaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {

        AccountHolderUpdateTaskDetailsDTO detailsDTO = new AccountHolderUpdateTaskDetailsDTO(taskDetailsDTO);
        detailsDTO.setAccountDetails(extractAccountDetails(detailsDTO));
        Long accountHolderIdentifier = accountService.getAccountFullIdentifier(detailsDTO.getAccountDetails()
                                                                                         .getAccountNumber()).getAccountHolder().getIdentifier();
        detailsDTO.setAccountHolderOwnership(populateAccountHolderOwnership(accountHolderIdentifier));

        switch (taskDetailsDTO.getTaskType()) {
            case ACCOUNT_HOLDER_UPDATE_DETAILS:
                detailsDTO.setAccountHolder(deserializeValues(taskDetailsDTO.getBefore(), AccountHolderDTO.class));
                detailsDTO.setAccountHolderDiff(deserializeValues(taskDetailsDTO.getDifference(), AccountHolderDetailsUpdateDiffDTO.class));
                break;
            case ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS:
            case ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE:
                detailsDTO.setAccountHolderContact(deserializeValues(taskDetailsDTO.getBefore(), AccountHolderRepresentativeDTO.class));
                detailsDTO.setAccountHolderContactDiff(deserializeValues(taskDetailsDTO.getDifference(), AccountHolderRepresentativeDTO.class));
                break;
            case ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD:
            case ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE:
                detailsDTO.setAccountHolderContact(deserializeValues(taskDetailsDTO.getBefore(), AccountHolderRepresentativeDTO.class));
                break;
            default:
                break;
        }
        return detailsDTO;
    }

    @Protected(
        {
            FourEyesPrincipleRule.class,
            ARsCanCompleteTaskNotInitiatedByAdministratorsRule.class,
            ARsCanApproveTaskWhenAccountHasSpecificStatusRule.class,
            RegistryAdminCanApproveTaskWhenAccountNotClosedOrPendingClosureRule.class,
            OnlySeniorRegistryAdminCanRejectTaskRule.class,
            OnlySeniorRegistryAdminCanApproveTask.class
        }
    )
    @Transactional
    @Override
    public TaskCompleteResponse complete(AccountHolderUpdateTaskDetailsDTO taskDTO,
                                         TaskOutcome taskOutcome,
                                         String comment) {

        if (TaskOutcome.APPROVED.equals(taskOutcome)) {

            Long accountHolderIdentifier;
            Account account;
            switch (taskDTO.getTaskType()) {
                case ACCOUNT_HOLDER_UPDATE_DETAILS:
                    account = accountService.getAccountFullIdentifier(taskDTO.getAccountDetails().getAccountNumber());
                    AccountDTO accountDto = accountAuditService.toAccountDto(account);

                     accountHolderIdentifier = deserializeValues(taskDTO.getBefore(), AccountHolderDTO.class).getId();
                    AccountHolderDTO holderForUpdateDTO = accountHolderService.getAccountHolder(accountHolderIdentifier);
                    AccountHolderDTO holderUpdatedValues = deserializeValues(taskDTO.getDifference(), AccountHolderDTO.class);
                    holderUpdatedValues.setId(accountHolderIdentifier);
                    copyAccountHolderDetailsProperties(holderUpdatedValues, holderForUpdateDTO);
                    accountService.updateHolder(holderForUpdateDTO);
                    //Traders accounts are not audited for diffs.
                    if (!RegistryAccountType.TRADING_ACCOUNT.equals(account.getRegistryAccountType())) {
                        accountAuditService.logChanges(accountDto, account, SourceSystem.REGISTRY);
                    }
                    break;
                case ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS:
                    account = accountService.getAccountFullIdentifier(taskDTO
                            .getAccountDetails()
                            .getAccountNumber());
                    accountHolderIdentifier = account.getAccountHolder().getIdentifier();
                    AccountHolderContactInfoDTO contactForUpdateDTO = accountHolderService.getAccountHolderContactInfo(accountHolderIdentifier);
                    AccountHolderRepresentativeDTO updatedValuesDTO = deserializeValues(taskDTO.getDifference(),
                                                                                        AccountHolderRepresentativeDTO.class);
                    copyAccountHolderContactProperties(updatedValuesDTO, contactForUpdateDTO.getPrimaryContact());
                    accountService.updateContact(accountHolderIdentifier, contactForUpdateDTO.getPrimaryContact(), true);
                    triggerAccountClaim(account.getIdentifier(), account.getRegistryAccountType());
                    break;
                case ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD:
                    account = accountService.getAccountFullIdentifier(taskDTO
                            .getAccountDetails()
                            .getAccountNumber());
                    AccountHolder accountHolder = account.getAccountHolder();
                    AccountHolderContactInfoDTO contactDTO = accountHolderService.getAccountHolderContactInfo(accountHolder.getIdentifier());
                    AccountHolderRepresentativeDTO newValuesDTO = deserializeValues(taskDTO.getBefore(),
                            AccountHolderRepresentativeDTO.class);
                    contactDTO.setAlternativeContact(newValuesDTO);
                    accountService.insertContact(accountHolder, contactDTO.getAlternativeContact(), false);
                    break;
                case ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE:
                    AccountHolderRepresentativeDTO deleteValuesDTO = deserializeValues(taskDTO.getBefore(),
                            AccountHolderRepresentativeDTO.class);
                    accountService.deleteContact(deleteValuesDTO);
                    break;
                case ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE:
                    account = accountService.getAccountFullIdentifier(taskDTO
                            .getAccountDetails()
                            .getAccountNumber());
                    accountHolderIdentifier = account.getAccountHolder()
                            .getIdentifier();
                    contactForUpdateDTO = accountHolderService.getAccountHolderContactInfo(accountHolderIdentifier);
                    updatedValuesDTO = deserializeValues(taskDTO.getDifference(),
                            AccountHolderRepresentativeDTO.class);
                    copyAccountHolderContactProperties(updatedValuesDTO, contactForUpdateDTO.getAlternativeContact());
                    accountService.updateContact(accountHolderIdentifier, contactForUpdateDTO.getAlternativeContact(), false);
                    triggerAccountClaim(account.getIdentifier(), account.getRegistryAccountType());
                    break;
                default:
                    break;
            }
        }
        
        if (RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS.equals(taskDTO.getTaskType())) {
            requestedDocsTaskService.completeChildRequestedDocumentTasks(taskDTO.getRequestId(),taskOutcome);
        }
        
        return defaultResponseBuilder(taskDTO).build();
    }

    @Protected({
        SeniorAdminCanClaimTaskInitiatedByAdminRule.class,
        OnlySeniorOrJuniorCanClaimTaskRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {

    }

    private AccountDetailsDTO extractAccountDetails(AccountHolderUpdateTaskDetailsDTO detailsDTO) {

        AccountDTO accountDTO = accountService.getAccountDTO(Long.valueOf(detailsDTO.getAccountNumber()));

        AccountDetailsDTO accountDetailsDTO = new AccountDetailsDTO();
        accountDetailsDTO.setAccountHolderName(accountDTO.getAccountHolder().getDetails().getName());
        accountDetailsDTO.setAccountNumber(accountDTO.getAccountDetails().getAccountNumber());
        accountDetailsDTO.setName(accountDTO.getAccountDetails().getName());
        accountDetailsDTO.setAccountType(accountDTO.getAccountType());
        return accountDetailsDTO;
    }

    private List<AccountHolderInAccountsDTO> populateAccountHolderOwnership(Long identifier) {
        List<Account> accounts = accountService.getAccountsByAccountHolder(identifier);
        if (accounts.size() > 1) {
            Map<RegistryAccountType, Long> accountsMap = accounts.stream()
                                                                 .collect(Collectors
                                                                              .groupingBy(Account::getRegistryAccountType,
                                                                                          Collectors.counting()));
            List<AccountHolderInAccountsDTO> list = new ArrayList<>();
            accountsMap.forEach((k,v) -> list.add(new AccountHolderInAccountsDTO(k.name(), v)));
            return list;
        }
        return new ArrayList<>();
    }

    private <T> T deserializeValues(String before, Class<T> clazz) {
        return mapper.convertToPojo(before, clazz);
    }

    private static void copyAccountHolderDetailsProperties(AccountHolderDTO src, AccountHolderDTO target) {
        
        copyNonNullProperties(src.getAddress(), target.getAddress());
        if (!src.getEmailAddress().isEmpty()) {
            copyNonNullProperties(src.getEmailAddress(), target.getEmailAddress());
        }
        if (!src.getPhoneNumber().isEmpty()) {
            copyNonNullProperties(src.getPhoneNumber(), target.getPhoneNumber());
        }
        if (target.getDetails() != null) {
            copyNonNullProperties(src.getDetails(), target.getDetails());
        }
    }

    private static void copyAccountHolderContactProperties(AccountHolderRepresentativeDTO src,
                                                           AccountHolderRepresentativeDTO target) {

        if (!src.getEmailAddress().isEmpty()) {
            copyNonNullProperties(src.getEmailAddress(), target.getEmailAddress());
        }
        copyNonNullProperties(src.getPhoneNumber(), target.getPhoneNumber());
        if (target.getDetails() != null) {
            copyNonNullProperties(src.getDetails(), target.getDetails());
        }
        copyNonNullProperties(src.getAddress(), target.getAddress());
        if (src.getPositionInCompany() != null) {
            target.setPositionInCompany(src.getPositionInCompany());
        }
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                     .map(FeatureDescriptor::getName)
                     .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                     .toArray(String[]::new);
    }

    private static void copyNonNullProperties(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    private void triggerAccountClaim(Long accountIdentifier, RegistryAccountType registryAccountType) {
        if (RegistryAccountType.OPERATOR_HOLDING_ACCOUNT.equals(registryAccountType) ||
                RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.equals(registryAccountType) ||
                RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT.equals(registryAccountType)) {
            final AccountDTO accountDTO = accountService.getAccountDTO(accountIdentifier);
            AccountContactSendInvitationDTO sendInvitationDTO =
                    AccountContactSendInvitationDTO.builder()
                            .metsContacts(new HashSet<>(accountDTO.getMetsContacts()))
                            .registryContacts(new HashSet<>(accountDTO.getRegistryContacts()))
                            .build();
            accountClaimService.sendInvitation(accountIdentifier, sendInvitationDTO);
        }
    }
}
