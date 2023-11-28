package gov.uk.ets.registry.api.helper.persistence;

import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.AccountContactType;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.user.domain.QUser;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.Optional;

/**
 * Helper methods for persisting accounts and relative entities.
 */
public class AccountModelTestHelper {

    private EntityManager entityManager;

    public AccountModelTestHelper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /***
     * Creates and persists an {@link Account} entity according to the passed command object
     * @param command The {@link AddAccountCommand} object
     * @return The new persisted {@link Account} entity
     */
    public Account addAccount(AddAccountCommand command) {
        Account account = new Account();
        account.setIdentifier(command.accountId);
        account.setFullIdentifier(command.fullIdentifier);
        account.setAccountName(command.accountName);
        account.setAccountStatus(command.accountStatus);
        account.setRegistryAccountType(command.registryAccountType);
        account.setKyotoAccountType(command.kyotoAccountType);
        account.setAccountHolder(command.accountHolder);
        account.setComplianceStatus(command.complianceStatus);
        account.setOpeningDate(new Date());
        account.setCommitmentPeriodCode(2020);
        account.setCheckDigits(19);
        account.setStatus(Status.ACTIVE);
        account.setAccountType(command.getAccountType() != null ? command.getAccountType() : "account type");
        account.setUnitType(UnitType.AAU);

        entityManager.persist(account);

        return account;
    }

    /***
     * Creates and persists a new {@link AccountHolder} entity according to the command argument
     * @param command The {@link AddAccountHolderCommand} object
     * @return The new persisted {@link AccountHolder}
     */
    public AccountHolder addAccountHolder(AddAccountHolderCommand command) {
        Contact contact = new Contact();
        contact.setPhoneNumber1("12345");
        contact.setPostCode("11111");
        entityManager.persist(contact);

        AccountHolder holder = new AccountHolder();
        holder.setName(command.name);
        holder.setFirstName(command.firstName);
        holder.setLastName(command.lastName);
        holder.setIdentifier(command.identifier);
        holder.setContact(contact);
        holder.setType(command.accountHolderType);
        entityManager.persist(holder);

        AccountHolderRepresentative primaryContact = new AccountHolderRepresentative();
        primaryContact.setFirstName("First name");
        primaryContact.setLastName("Last name");
        primaryContact.setAlsoKnownAs("Also known as");
        primaryContact.setAccountHolder(holder);
        primaryContact.setAccountContactType(AccountContactType.PRIMARY);
        entityManager.persist(primaryContact);

        return holder;
    }

    /***
     * Creates and persists a new {@link User} entity and links it to the account as
     * the command instructs.
     * @param command The {@link AddAccountHolderCommand} object
     * @return The new persisted {@link AccountHolder}
     */
    public Account addUserToAccountAccess(AddUserToAccountAccessCommand command) {
        User user =
            new JPAQuery<User>(entityManager).from(QUser.user).where(QUser.user.urid.eq(command.urid)).fetchOne();

        if (user == null) {
            user = new User();
        }

        user.setState(command.getUserStatus() == null ? UserStatus.ENROLLED : command.getUserStatus());
        user.setUrid(command.urid);
        user.setEnrolmentKey(Optional.ofNullable(command.enrollmentKey)
            .orElse("GB-234-534-5"));
        entityManager.persist(user);

        AccountAccess accountAccess = new AccountAccess();
        accountAccess.setAccount(command.account);
        accountAccess.setUser(user);
        accountAccess.setRight(command.right != null ? command.right : AccountAccessRight.INITIATE_AND_APPROVE);
        accountAccess.setState(command.state);
        entityManager.persist(accountAccess);

        return command.account;
    }

    /**
     * Creates and persists an installation linked to the account entity of the command.
     *
     * @param command The {@link AddInstallationEntityToAccountCommand} command
     * @return the {@link Account} entity.
     */
    public Account addInstallationToAccount(AddInstallationEntityToAccountCommand command) {
        Installation installation = new Installation();
        installation.setInstallationName(command.name);
        installation.setStartYear(2016);
        installation.setEndYear(2020);
        installation.setIdentifier(command.identifier);
        installation.setRegulator(command.regulatorType);
        installation.setPermitIdentifier(command.permitId);
        installation.setActivityType(command.getActivityType());
        entityManager.persist(installation);
        command.account.setCompliantEntity(installation);
        entityManager.persist(command.account);
        return command.account;
    }

    /***
     * Creates and perists a new {@link AircraftOperator} and links it to the {@link Account} entity of the command argument
     * @param command The {@link AddAircraftEntityToAccountCommand} command
     * @return The updated {@link Account} of command
     */
    public Account addAircraftToAccount(AddAircraftEntityToAccountCommand command) {
        AircraftOperator aircraftOperator = new AircraftOperator();
        aircraftOperator.setMonitoringPlanIdentifier(command.monitoringPlanId);
        aircraftOperator.setRegulator(command.regulatorType);
        aircraftOperator.setStartYear(2017);
        aircraftOperator.setEndYear(2020);
        aircraftOperator.setIdentifier(command.identifier);
        entityManager.persist(aircraftOperator);
        command.account.setCompliantEntity(aircraftOperator);
        entityManager.persist(command.account);

        return command.account;
    }

    /**
     * Command object for creating and persisting an {@link AccountHolder} entity
     */
    @Builder
    @Getter
    public static class AddAccountHolderCommand {
        /**
         * The name of the account holder
         */
        private String name;

        /**
         * The individual first name
         */
        private String firstName;

        /**
         * The individual last name
         */
        private String lastName;

        /**
         * The identifier of the account holder
         */
        private Long identifier;
        /**
         * The account holder type
         */
        private AccountHolderType accountHolderType;
        /**
         * The status of the account holder
         */
        private Status status;
    }

    /**
     * Command object for creating and persisting an {@link Account} entity
     */
    @Builder
    @Getter
    public static class AddAccountCommand {
        /**
         * The account identifier of the new account
         */
        private Long accountId;
        /**
         * The account full identifier of the new account
         */
        private String fullIdentifier;
        /**
         * The account name of the new account
         */
        private String accountName;
        /**
         * The account status of the new account
         */
        private AccountStatus accountStatus;
        /**
         * The registry account type of the new account
         */
        private RegistryAccountType registryAccountType;
        /**
         * The kyoto account type of the new account
         */
        private KyotoAccountType kyotoAccountType;
        /**
         * The account holder of the new account
         */
        private AccountHolder accountHolder;
        /**
         * The compliance status of the new account
         */
        private ComplianceStatus complianceStatus;

        private String accountType;
    }

    /**
     * Command object for creating account's installation
     */
    @Builder
    @Getter
    public static class AddInstallationEntityToAccountCommand {
        /**
         * The name of the new installation
         */
        private String name;
        /**
         * The identifier of the new installation
         */
        private Long identifier;
        /**
         * The regulator type of the new installation
         */
        private RegulatorType regulatorType;
        /**
         * The permit id of the new installation
         */
        private String permitId;
        /**
         * The {@link Account} account which relates to this installation
         */
        private Account account;

        private String activityType;
    }

    /**
     * Command object for creating account's aircraft entity
     */
    @Builder
    @Getter
    public static class AddAircraftEntityToAccountCommand {
        /**
         * The name of aircraft entity
         */
        private String name;
        /**
         * The identifier of the aircraft entity
         */
        private Long identifier;
        /**
         * The regulator type of the aircraft entity
         */
        private RegulatorType regulatorType;
        /**
         * The monitoring plan id of the aircraft entity
         */
        private String monitoringPlanId;
        /**
         * The {@link Account} account which relates to the aircraft entity
         */
        private Account account;
    }

    /**
     * Command object for adding user to the account access relationship
     */
    @Builder
    @Getter
    @Setter
    public static class AddUserToAccountAccessCommand {
        /**
         * The business identifier of the new user
         */
        private String urid;
        /**
         * The enrollment key of the new user
         */
        private String enrollmentKey;
        /**
         * The account of the user
         */
        private Account account;
        /**
         * The current state
         */
        private AccountAccessState state;
        /**
         * The user status
         */
        private UserStatus userStatus;

        private AccountAccessRight right;
    }

}
