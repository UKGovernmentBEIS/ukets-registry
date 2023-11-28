package gov.uk.ets.registry.api.user.admin.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.QAccount;
import gov.uk.ets.registry.api.account.domain.QAccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.common.exception.NotFoundException;
import gov.uk.ets.registry.api.common.publication.PublicationRequestAddRemoveRoleService;
import gov.uk.ets.registry.api.common.reports.ReportRequestAddRemoveRoleService;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.user.admin.shared.EnrolledUserDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service for authority users administration.
 */
@Service
@RequiredArgsConstructor
public class AuthorityAdministrationService {
    private static final String ENROLLED_USER_NOT_FOUND = "User not enrolled or User ID not found.";
    private static final String AUTHORITY_USER_NOT_FOUND = "User is not an authority user.";
    private static final String USER_IS_AN_AUTHORITY_USER = "User is already an authority user.";
    private static final String USER_NOT_FOUND_ON_KEYCLOAK = "The user of user id does not exist on keycloak";

    private final UserRepository userRepository;
    private final UserAdministrationService administrationService;
    private final ReportRequestAddRemoveRoleService reportRequestAddRemoveRoleService;
    private final AccountAccessRepository accountAccessRepository;
    private final AccountRepository accountRepository;
    private final PublicationRequestAddRemoveRoleService publicationRequestAddRemoveRoleService;

    /**
     * Gets an Enrolled user by user unique business identifier.
     */
    public EnrolledUserDTO getEnrolledUser(String urid) {
        User user = userRepository.findByUrid(urid);
        if (user == null || !user.getState().equals(UserStatus.ENROLLED)) {
            throw new NotFoundException(ENROLLED_USER_NOT_FOUND);
        }
        UserRepresentation userRepresentation = administrationService.findByIamId(user.getIamIdentifier());
        if (userRepresentation == null) {
            throw new IllegalStateException(USER_NOT_FOUND_ON_KEYCLOAK);
        }
        return EnrolledUserDTO.builder()
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(userRepresentation.getEmail())
            .userId(user.getUrid())
            .knownAs(user.getKnownAs())
            .build();
    }

    /**
     * Makes a user authority user
     * @param urid
     */
    @Transactional
    public void setAuthorityUser(String urid) {
        User user = userRepository.findByUrid(urid);
        if (user == null || !user.getState().equals(UserStatus.ENROLLED)) {
            throw new IllegalArgumentException(ENROLLED_USER_NOT_FOUND);
        }
        if (administrationService.hasUserRole(user.getIamIdentifier(), UserRole.AUTHORITY_USER.getKeycloakLiteral())) {
            throw new IllegalArgumentException(USER_IS_AN_AUTHORITY_USER);
        }
        administrationService.addUserRole(user.getIamIdentifier(), UserRole.AUTHORITY_USER.getKeycloakLiteral());
        administrationService.addUserRole(user.getIamIdentifier(), UserRole.AUTHORISED_REPRESENTATIVE.getKeycloakLiteral());
        // request to add reports-user role in reports-api
        reportRequestAddRemoveRoleService.requestReportsApiAddRole(user.getIamIdentifier());
        publicationRequestAddRemoveRoleService.requestPublicationApiAddRole(user.getIamIdentifier());
        findCentralAccounts()
            .forEach(a -> {
                AccountAccess accountAccess = new AccountAccess();
                accountAccess.setUser(user);
                accountAccess.setState(AccountAccessState.ACTIVE);
                accountAccess.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
                accountAccess.setAccount(a);
                accountAccessRepository.save(accountAccess);
            });
        findReadOnlyCentralAccounts()
            .forEach(a -> {
                AccountAccess accountAccess = new AccountAccess();
                accountAccess.setUser(user);
                accountAccess.setState(AccountAccessState.ACTIVE);
                accountAccess.setRight(AccountAccessRight.READ_ONLY);
                accountAccess.setAccount(a);
                accountAccessRepository.save(accountAccess);
            });
    }

    @Transactional
    public void removeUserFromAuthorityUsers(String urid) {
        User user = userRepository.findByUrid(urid);
        if (user == null || !user.getState().equals(UserStatus.ENROLLED)) {
            throw new IllegalArgumentException(ENROLLED_USER_NOT_FOUND);
        }
        if (!administrationService.hasUserRole(user.getIamIdentifier(), UserRole.AUTHORITY_USER.getKeycloakLiteral())) {
            throw new IllegalArgumentException(AUTHORITY_USER_NOT_FOUND);
        }

        administrationService.removeUserRole(user.getIamIdentifier(), UserRole.AUTHORITY_USER.getKeycloakLiteral());
        // request to remove reports-user role from reports-api
        reportRequestAddRemoveRoleService.requestReportsApiRemoveRole(user.getIamIdentifier());
        publicationRequestAddRemoveRoleService.requestPublicationApiRemoveRole(user.getIamIdentifier());

        List<Account> centralAccounts =  StreamSupport
            .stream(findCentralAccounts().spliterator(), false)
            .collect(Collectors.toList());
        List<Account> readOnlyCentralAccounts = StreamSupport
            .stream(findReadOnlyCentralAccounts().spliterator(), false)
            .collect(Collectors.toList());
        centralAccounts.addAll(readOnlyCentralAccounts);
        Iterable nonCentralAccountAccesses = accountAccessRepository.findAll(QAccountAccess.accountAccess.user.eq(user)
            .and(QAccountAccess.accountAccess.account
                .notIn(centralAccounts)));
        if (!nonCentralAccountAccesses.iterator().hasNext()) {
            administrationService.removeUserRole(user.getIamIdentifier(), UserRole.AUTHORISED_REPRESENTATIVE.getKeycloakLiteral());
        }

        accountAccessRepository.deleteInBatch(accountAccessRepository
            .findAll(QAccountAccess.accountAccess.user.eq(user)
            .and(QAccountAccess.accountAccess.account
            .in(centralAccounts))));
    }

    private Iterable<Account> findCentralAccounts() {
        return accountRepository.findAll(QAccount.account.accountType
            .in(AccountType.getCentralTypes().stream().map(AccountType::getLabel).collect(Collectors.toList())));
    }

    private Iterable<Account> findReadOnlyCentralAccounts() {
        return accountRepository.findAll(QAccount.account.accountType
            .in(AccountType.getReadOnlyCentralTypes().stream().map(AccountType::getLabel).collect(Collectors.toList())));
    }
}
