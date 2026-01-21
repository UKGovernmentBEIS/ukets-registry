package gov.uk.ets.registry.api.helper.integration;

import gov.uk.ets.registry.api.account.domain.*;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.repository.*;
import gov.uk.ets.registry.api.account.web.model.accountcontact.OperatorType;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.common.model.repositories.ContactRepository;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.compliance.domain.ExcludeEmissionsEntry;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.EmissionsEntry;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Transactional
public class TestDataHelper {

    public static final String METS_CONTACT_NAME_TO_OVERWRITE = "MetsContactNameToOverwrite";

    private final ContactRepository contactRepository;
    private final AccountHolderRepository accountHolderRepository;
    private final AccountRepository accountRepository;
    private final InstallationRepository installationRepository;
    private final AircraftOperatorRepository aircraftOperatorRepository;
    private final CompliantEntityRepository compliantEntityRepository;
    private final EntityManager entityManager;

    public Account createClosedInstallationAccount(TestAccountData data) {
        return createInstallationAccount(data, AccountStatus.CLOSED);
    }

    public Account createInstallationAccount(TestAccountData data) {
        return createInstallationAccount(data, AccountStatus.OPEN);
    }

    public Account createInstallationAccount(TestAccountData data, AccountStatus status) {
        // 1️ Create contact
        Contact contact = new Contact();
        contactRepository.saveAndFlush(contact);

        // 2 Create holder
        AccountHolder holder = new AccountHolder();
        holder.setIdentifier(data.getHolderId());
        holder.setType(AccountHolderType.ORGANISATION);
        holder.setName("Initial Holder");
        holder.setContact(contact);
        accountHolderRepository.saveAndFlush(holder);

        // 3 Create account
        Account account = new Account();
        account.setIdentifier(data.getAccountId());
        account.setFullIdentifier(data.getAccountIdentifier());
        account.setAccountName("TestName");
        account.setAccountType("TestType");
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account.setKyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        account.setAccountHolder(holder);
        account.setAccountStatus(status);
        account.setOpeningDate(new Date());
        account.setStatus(Status.ACTIVE);
        account.setCommitmentPeriodCode(123456);
        account.setCheckDigits(123456);
        accountRepository.saveAndFlush(account);

        // 4️ Create installation + link both sides
        Installation installation = new Installation();
        installation.setEmitterId("654321");
        installation.setPermitIdentifier("PERMIT-123");
        installation.setIdentifier(data.getOperatorId());
        installation.setStartYear(2025);
        installation.setAccount(account);
        installation.setRegulator(RegulatorType.EA);
        account.setCompliantEntity(installation);

        //5 Create activity type
        ActivityType activityType = new ActivityType();
        activityType.setDescription(InstallationActivityType.CAPTURE_OF_GREENHOUSE_GASES.toString());
        activityType.setInstallation(installation);
        installation.setActivityTypes(Set.of(activityType));

        installationRepository.saveAndFlush(installation);
        accountRepository.saveAndFlush(account);

        return account;
    }

    public Account createAircraftOperatorAccount(TestAccountData data) {

        Contact contact = new Contact();
        contactRepository.save(contact);

        AccountHolder holder = new AccountHolder();
        holder.setIdentifier(data.getHolderId());
        holder.setType(AccountHolderType.ORGANISATION);
        holder.setName("Initial Holder AIR");
        holder.setContact(contact);
        accountHolderRepository.save(holder);

        Account account = new Account();
        account.setIdentifier(data.getAccountId());
        account.setFullIdentifier(data.getAccountIdentifier());
        account.setAccountName("AIRCRAFT TestName");
        account.setAccountType("AIRCRAFT TestType");
        account.setRegistryAccountType(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);
        account.setKyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        account.setAccountHolder(holder);
        account.setAccountStatus(AccountStatus.OPEN);
        account.setOpeningDate(new Date());
        account.setStatus(Status.ACTIVE);
        account.setCommitmentPeriodCode(223456);
        account.setCheckDigits(223456);

        accountRepository.save(account);

        AircraftOperator aircraftOperator = new AircraftOperator();
        aircraftOperator.setEmitterId("654322");
        aircraftOperator.setMonitoringPlanIdentifier("654322");
        aircraftOperator.setIdentifier(data.getOperatorId());
        aircraftOperator.setStartYear(2023);
        aircraftOperator.setAccount(account);

        aircraftOperatorRepository.save(aircraftOperator);

        account.setCompliantEntity(aircraftOperator);
        accountRepository.save(account);

        accountRepository.flush();

        return account;
    }

    public Account createMaritimeOperatorAccount(TestAccountData data) {

        // 1️Create contact
        Contact contact = new Contact();
        contactRepository.saveAndFlush(contact);

        // 2️Create account holder
        AccountHolder holder = new AccountHolder();
        holder.setIdentifier(data.getHolderId());
        holder.setType(AccountHolderType.ORGANISATION);
        holder.setName("Initial Holder MRT");
        holder.setContact(contact);
        accountHolderRepository.saveAndFlush(holder);

        // 3️Create account
        Account account = new Account();
        account.setIdentifier(data.getAccountId());
        account.setFullIdentifier(data.getAccountIdentifier());
        account.setAccountName("MARITIME TestName");
        account.setAccountType("MARITIME TestType");
        account.setRegistryAccountType(RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT);
        account.setKyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        account.setAccountHolder(holder);
        account.setAccountStatus(AccountStatus.OPEN);
        account.setOpeningDate(new Date());
        account.setStatus(Status.ACTIVE);
        account.setCommitmentPeriodCode(323456);
        account.setCheckDigits(323456);
        accountRepository.saveAndFlush(account);

        // 4️Create maritime operator + link
        MaritimeOperator maritimeOperator = new MaritimeOperator();
        maritimeOperator.setIdentifier(data.getOperatorId());
        maritimeOperator.setMaritimeMonitoringPlanIdentifier("10000001");
        maritimeOperator.setImo("IMO-0000000");
        maritimeOperator.setStartYear(2023);
        maritimeOperator.setAccount(account);
        maritimeOperator.setEmitterId("10000001");

        account.setCompliantEntity(maritimeOperator);
        compliantEntityRepository.saveAndFlush(maritimeOperator);
        accountRepository.saveAndFlush(account);

        return account;
    }

    public Account createMaritimeAccountWithIMO(
            TestAccountData data,
            String imo
    ) {
        Account acc = createMaritimeOperatorAccount(data);

        MaritimeOperator op = (MaritimeOperator) acc.getCompliantEntity();
        op.setImo(imo);
        compliantEntityRepository.saveAndFlush(op);

        return acc;
    }

    public Account createMaritimeAccountWithMonitoringPlan(
            TestAccountData data,
            String mpId
    ) {
        Account acc = createMaritimeOperatorAccount(data);

        MaritimeOperator op = (MaritimeOperator) acc.getCompliantEntity();
        op.setMaritimeMonitoringPlanIdentifier(mpId);
        compliantEntityRepository.saveAndFlush(op);

        return acc;
    }

    public void addEmissionsEntry(Long compliantEntityId, int year, Long emissions) {
        EmissionsEntry e = new EmissionsEntry();
        e.setCompliantEntityId(compliantEntityId);
        e.setYear((long) year);
        e.setUploadDate(LocalDateTime.now());
        e.setEmissions(emissions);
        entityManager.persist(e);
    }

    public void addExcludedEmissionsEntry(Long compliantEntityId, int year) {
        ExcludeEmissionsEntry ex = new ExcludeEmissionsEntry();
        ex.setCompliantEntityId(compliantEntityId);
        ex.setYear((long) year);
        ex.setLastUpdated(new Date());
        ex.setExcluded(true);
        entityManager.persist(ex);
    }

    public Account createAircraftOperatorAccountWithMetsContacts(TestAccountData data) {
        Account account = createAircraftOperatorAccount(data);

        for (MetsAccountContact c : metsAccountContacts()) {
            c.setAccount(account);
            account.getMetsAccountContacts().add(c);
        }
        return accountRepository.saveAndFlush(account);
    }

    public Account createInstallationAccountWithMetsContacts(TestAccountData data) {
        Account account = createInstallationAccount(data);

        for (MetsAccountContact c : metsAccountContacts()) {
            c.setAccount(account);
            account.getMetsAccountContacts().add(c);
        }
        return accountRepository.saveAndFlush(account);
    }

    public Account createMaritimeAccountWithMetsContacts(TestAccountData data) {
        Account account = createMaritimeOperatorAccount(data);

        for (MetsAccountContact c : metsAccountContacts()) {
            c.setAccount(account);
            account.getMetsAccountContacts().add(c);
        }
        return accountRepository.saveAndFlush(account);
    }

    private List<MetsAccountContact> metsAccountContacts() {

        List<MetsAccountContact> contacts = new ArrayList<>();

        contacts.add(
            MetsAccountContact.builder()
                .name(METS_CONTACT_NAME_TO_OVERWRITE)
                .emailAddress("contact1@example.com")
                .operatorType(OperatorType.OPERATOR_ADMIN)
                .phoneNumber1("123456789")
                .countryCode1("GB")
                .invitedOn(LocalDate.now().atStartOfDay())
                .build()
        );

        return contacts;
    }
}
