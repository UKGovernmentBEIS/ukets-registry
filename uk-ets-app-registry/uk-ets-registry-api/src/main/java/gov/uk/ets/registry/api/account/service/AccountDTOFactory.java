package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.MaritimeOperator;
import gov.uk.ets.registry.api.account.domain.SalesContact;
import gov.uk.ets.registry.api.account.domain.types.AccountContactType;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepresentativeRepository;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderContactInfoDTO;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.ContactDTO;
import gov.uk.ets.registry.api.account.web.model.SalesContactDetailsDTO;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.common.ConversionService;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.domain.data.TrustedAccountListRulesDTO;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.UserConversionService;
import gov.uk.ets.registry.api.user.UserDTO;
import gov.uk.ets.registry.api.user.domain.UserWorkContact;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@AllArgsConstructor
public class AccountDTOFactory {
    private AccountConversionService accountConversionService;

    private AccountHolderRepresentativeRepository accountHolderRepresentativeRepository;

    private ConversionService conversionService;

    private UserConversionService userConversionService;

    private UserService userService;

    private AuthorizedRepresentativeService authorizedRepresentativeService;

    private final AccountAccessRepository accountAccessRepository;

    private final AuthorizationService authorizationService;

    @Transactional(readOnly = true)
    public AccountDTO create(Account account) {
        return this.create(account, false);
    }

    /**
     * TODO: Refactor this method according to the comments inside it. Creates an {@link AccountDTO} object from an
     * TODO: remove the no userTOken parameter; ITS just a dirty hack to support the proposed accounts migration
     * <p>
     * {@link Account} object
     *
     * @param account     the account input
     * @param noUserToken indicates that no user token is available.
     * @return a new Account DTO
     */
    @Transactional(readOnly = true)
    public AccountDTO create(Account account, boolean noUserToken) {
        if (account == null) {
            throw new IllegalArgumentException("Unable to create account dto from nullable");
        }
        AccountDTO accountDTO = new AccountDTO();
        accountDTO
            .setAccountType(account.getRegistryAccountType() != RegistryAccountType.NONE ?
                account.getRegistryAccountType().name() :
                account.getKyotoAccountType().name());
        accountDTO.setIdentifier(account.getIdentifier());
        setupTrustedAccountListRulesDTO(accountDTO, account);
        setupAccountDetailsDTO(accountDTO, account);
        setupOperator(accountDTO, account);
        setupAuthorizedRepresentatives(accountDTO, account);
        accountDTO.setBalance(account.getBalance());
        accountDTO.setUnitType(account.getUnitType());
        AccountType accountType = AccountType.get(account.getRegistryAccountType(), account.getKyotoAccountType());
        accountDTO.setGovernmentAccount(accountType.isGovernmentAccount());
        accountDTO.setKyotoAccountType(accountType.getKyoto());
        accountDTO.setAccountType(accountType.name());
        Optional.ofNullable(account.getBillingAddressSameAsAccountHolderAddress())
                .ifPresent(accountDTO::setAccountDetailsSameBillingAddress);

        if (!noUserToken) {
            accountDTO.setPendingARRequests(authorizedRepresentativeService.getPendingActions(account.getIdentifier()));
        }

        Map<RequestType, Integer> arTransitions = authorizedRepresentativeService.calculateCounters(account.getIdentifier());
        accountDTO.setAddedARs(arTransitions.getOrDefault(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST, 0));
        accountDTO.setRemovedARs(arTransitions.getOrDefault(RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST, 0));

        return accountDTO;
    }

    /**
     * Set transaction rules to the AccountDTO object.
     *
     * @param accountDTO the AccountDTO object.
     * @param account the account.
     */
    public void setupTrustedAccountListRulesDTO(AccountDTO accountDTO, Account account) {
        TrustedAccountListRulesDTO trustedAccountListRulesDTO = new TrustedAccountListRulesDTO();
        trustedAccountListRulesDTO.setRule1(account.getApprovalOfSecondAuthorisedRepresentativeIsRequired());
        trustedAccountListRulesDTO.setRule2(account.getTransfersToAccountsNotOnTheTrustedListAreAllowed());
        trustedAccountListRulesDTO.setRule3(account.getSinglePersonApprovalRequired());
        accountDTO.setTrustedAccountListRules(trustedAccountListRulesDTO);
    }

    private void setupAccountDetailsDTO(AccountDTO accountDTO, Account account) {
        AccountDetailsDTO accountDetailsDTO = new AccountDetailsDTO();
        accountDetailsDTO.setName(account.getAccountName());
        accountDetailsDTO.setPublicAccountIdentifier(account.getPublicIdentifier());
        accountDetailsDTO.setAccountType(account.getAccountType());
        accountDetailsDTO.setAccountNumber(account.getFullIdentifier());
        accountDetailsDTO.setAccountStatus(account.getAccountStatus());
        accountDetailsDTO.setOpeningDate(account.getOpeningDate());
        accountDetailsDTO.setClosingDate(account.getClosingDate());
        accountDetailsDTO.setClosureReason(account.getClosureReason());
        if(authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)) {
	        accountDetailsDTO.setExcludedFromBilling(account.isExcludedFromBilling());
	        accountDetailsDTO.setExcludedFromBillingRemarks(account.getExcludedFromBillingRemarks());
        }
        setupAccountHolderDTO(accountDTO, accountDetailsDTO, account);
        setupSalesContactDetailsDTO(accountDetailsDTO, account);
        Optional.ofNullable(account.getContact()).ifPresent(
            contact -> {
                accountDetailsDTO.setAddress(conversionService.getAddressFromContact(contact));
                accountDetailsDTO.setBillingContactDetails(conversionService.getBillingDetailsFromContact(contact));
            });
        accountDTO.setAccountDetails(accountDetailsDTO);
    }

    private void setupAccountHolderDTO(AccountDTO accountDTO, AccountDetailsDTO accountDetailsDTO, Account account) {
        AccountHolder accountHolder = account.getAccountHolder();
        if (accountHolder == null) {
            return;
        }
        AccountHolderDTO accountHolderDTO = accountConversionService.convert(accountHolder);
        accountDTO.setAccountHolder(accountHolderDTO);

        List<AccountHolderRepresentative> accountHolderRepresentatives =
            accountHolderRepresentativeRepository.getAccountHolderRepresentatives(accountHolder.getIdentifier());

        AccountHolderContactInfoDTO accountHolderContactInfo = new AccountHolderContactInfoDTO();
        // TODO: In order to support the old data, not exception throws if primary contact is null but it should.
        Optional<AccountHolderRepresentative> primaryContact = accountHolderRepresentatives.stream()
            .filter(accountHolderRepresentative ->
                AccountContactType.PRIMARY.equals(accountHolderRepresentative.getAccountContactType())).findFirst();
        primaryContact.ifPresent(accountHolderRepresentative -> accountHolderContactInfo
            .setPrimaryContact(accountConversionService.convert(accountHolderRepresentative)));
        // TODO: Some further checks for more than one alternative contacts should be made here.
        Optional<AccountHolderRepresentative> alternativeContact = accountHolderRepresentatives.stream()
            .filter(accountHolderRepresentative ->
                AccountContactType.ALTERNATIVE.equals(accountHolderRepresentative.getAccountContactType())).findFirst();
        alternativeContact.ifPresent(accountHolderRepresentative -> accountHolderContactInfo
            .setAlternativeContact(accountConversionService.convert(accountHolderRepresentative)));
        accountDTO.setAccountHolderContactInfo(accountHolderContactInfo);

        accountDetailsDTO.setAccountHolderName(accountHolder.getName());
        accountDetailsDTO.setAccountHolderId(String.valueOf(accountHolder.getIdentifier()));
        accountDetailsDTO.setComplianceStatus(account.getComplianceStatus());
    }

    public void setupOperator(AccountDTO dto, Account account) {
        CompliantEntity compliantEntity = account.getCompliantEntity();
        if (account.getCompliantEntity() == null) {
            return;
        }
        Object noProxyCompliantEntity = Hibernate.unproxy(compliantEntity);
        if (noProxyCompliantEntity instanceof Installation) {
            dto.setOperator(accountConversionService.convert((Installation) noProxyCompliantEntity));
        } else if (noProxyCompliantEntity instanceof AircraftOperator) {
            dto.setOperator(accountConversionService.convert((AircraftOperator) noProxyCompliantEntity));
        } else if (noProxyCompliantEntity instanceof MaritimeOperator) {
            dto.setOperator(accountConversionService.convert((MaritimeOperator) noProxyCompliantEntity));
        }
    }

    private void setupAuthorizedRepresentatives(AccountDTO accountDTO, Account account) {
        List<AuthorisedRepresentativeDTO> authorisedRepresentatives = new ArrayList<>();

        Map<String, AccountAccess> userAccess = accountAccessRepository.finARsByAccount_Identifier(account.getIdentifier())
            .stream()
            .collect(Collectors.toMap(accountAccess -> accountAccess.getUser().getUrid(), Function.identity()));

        if (!userAccess.isEmpty()) {
            userService.getUserWorkContacts(userAccess.keySet())
                .forEach(userWorkContact -> {
                    AccountAccess accountAccess = userAccess.get(userWorkContact.getUrid());
                    AuthorisedRepresentativeDTO ar = new AuthorisedRepresentativeDTO();
                    ar.setRight(accountAccess.getRight());
                    ar.setState(accountAccess.getState());
                    UserDTO user = userConversionService.convert(accountAccess.getUser());
                    ar.setUser(user);
                    ar.setUrid(user.getUrid());
                    ContactDTO workContact = convert(userWorkContact);
                    ar.setContact(workContact);
                    authorisedRepresentatives.add(ar);
                });
        }

        accountDTO.setAuthorisedRepresentatives(authorisedRepresentatives);
    }

    private ContactDTO convert(UserWorkContact userWorkContact) {
        ContactDTO contact = new ContactDTO();
        contact.setEmailAddress(userWorkContact.getEmail());
        contact.setLine1(userWorkContact.getWorkBuildingAndStreet());
        contact.setLine2(userWorkContact.getWorkBuildingAndStreetOptional());
        contact.setLine3(userWorkContact.getWorkBuildingAndStreetOptional2());
        contact.setPostCode(userWorkContact.getWorkPostCode());
        contact.setCity(userWorkContact.getWorkTownOrCity());
        contact.setStateOrProvince(userWorkContact.getWorkStateOrProvince());
        contact.setCountry(userWorkContact.getWorkCountry());
        contact.setMobileCountryCode(userWorkContact.getWorkMobileCountryCode());
        contact.setMobilePhoneNumber(userWorkContact.getWorkMobilePhoneNumber());
        contact.setAlternativeCountryCode(userWorkContact.getWorkAlternativeCountryCode());
        contact.setAlternativePhoneNumber(userWorkContact.getWorkAlternativePhoneNumber());
        contact.setNoMobilePhoneNumberReason(userWorkContact.getNoMobilePhoneNumberReason());
        return contact;
    }
    
    private void setupSalesContactDetailsDTO(AccountDetailsDTO accountDetailsDTO, Account account) {
    	SalesContact salesContact = account.getSalesContact();
        if (salesContact == null) {
            return;
        }
        SalesContactDetailsDTO salesContactDetailsDTO = accountConversionService.convert(salesContact);
        accountDetailsDTO.setSalesContactDetails(salesContactDetailsDTO);
    }
}
