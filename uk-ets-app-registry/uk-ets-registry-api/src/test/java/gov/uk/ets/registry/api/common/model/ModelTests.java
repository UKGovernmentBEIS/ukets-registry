package gov.uk.ets.registry.api.common.model;

import gov.uk.ets.registry.api.account.domain.*;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.repository.*;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.common.model.repositories.ContactRepository;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create"
})
public class ModelTests {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AircraftOperatorRepository aircraftOperatorRepository;

    @Autowired
    private CompliantEntityRepository compliantEntityRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private InstallationRepository installationRepository;

    @Autowired
    private AccountHolderRepresentativeRepository legalRepresentativeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountAccessRepository accountAccessRepository;

    @Test
    public void testWiring() {
        Assert.assertNotNull(entityManager);
        Assert.assertNotNull(accountHolderRepository);
        Assert.assertNotNull(accountRepository);
        Assert.assertNotNull(aircraftOperatorRepository);
        Assert.assertNotNull(compliantEntityRepository);
        Assert.assertNotNull(contactRepository);
        Assert.assertNotNull(installationRepository);
        Assert.assertNotNull(legalRepresentativeRepository);
        Assert.assertNotNull(userRepository);
        Assert.assertNotNull(accountAccessRepository);
    }

    @Test
    @Transactional
    public void testTraditionalJPA() {
        Assert.assertNotNull(entityManager);

        User user = new User();
        user.setUrid("GR12345");

        entityManager.persist(user);
        Assert.assertNotNull(user.getId());

        User user1 = entityManager.find(User.class, user.getId());
        Assert.assertNotNull(user.equals(user1));
    }

    @Test
    @Transactional
    public void testPersistence() {
        Contact contact = new Contact();
        contact.setPhoneNumber1("12345");
        contact.setPostCode("11111");
        contactRepository.save(contact);

        AccountHolder holder = new AccountHolder();
        holder.setName("AH name");
        holder.setIdentifier(12L);
        holder.setContact(contact);
        holder.setType(AccountHolderType.ORGANISATION);
        accountHolderRepository.save(holder);

        AccountHolderRepresentative legal = new AccountHolderRepresentative();
        legal.setFirstName("First name");
        legal.setLastName("Last name");
        legal.setAccountHolder(holder);
        legalRepresentativeRepository.save(legal);

        Account account = new Account();
        account.setAccountName("AC name");
        account.setIdentifier(123L);
        account.setAccountHolder(holder);
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account.setStatus(Status.REQUESTED);
        accountRepository.save(account);

        Installation installation = new Installation();
        installation.setInstallationName("Factory 1");
        installation.setStartYear(2016);
        installation.setEndYear(2020);
        installation.setIdentifier(12345L);
        installation.setAccount(account);
        installationRepository.save(installation);

        User user = new User();
        user.setState(UserStatus.REGISTERED);
        user.setUrid("GR12312312");
        user.setEnrolmentKey("GB-234-534-5");
        userRepository.save(user);

        Assert.assertTrue(contactRepository.count() > 0);
        Assert.assertTrue(accountHolderRepository.count() > 0);
        Assert.assertTrue(legalRepresentativeRepository.count() > 0);
        Assert.assertTrue(accountRepository.count() > 0);
        Assert.assertTrue(installationRepository.count() > 0);
        Assert.assertTrue(userRepository.count() > 0);
    }

    @Test
    @Transactional
    public void testAicraftMonitoringPlan() {
        Contact contact = new Contact();
        contact.setPhoneNumber1("12345");
        contact.setPostCode("11111");
        contactRepository.save(contact);

        AccountHolder holder = new AccountHolder();
        holder.setName("AH name");
        holder.setIdentifier(5L);
        holder.setContact(contact);
        holder.setType(AccountHolderType.ORGANISATION);
        accountHolderRepository.save(holder);

        AccountHolderRepresentative legal = new AccountHolderRepresentative();
        legal.setFirstName("First name");

        legal.setLastName("Last name");
        legal.setAccountHolder(holder);
        legalRepresentativeRepository.save(legal);

        AircraftOperator aircraftOperator = new AircraftOperator();
        aircraftOperator.setMonitoringPlanIdentifier("testSuccess");
        aircraftOperator.setIdentifier(15L);

        Account account = new Account();
        account.setAccountName("AC name");
        account.setIdentifier(123L);
        account.setCompliantEntity(aircraftOperator);
        account.setAccountHolder(holder);
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account.setStatus(Status.REQUESTED);
        account.setAccountStatus(AccountStatus.OPEN);

        aircraftOperator.setAccount(account);
        accountRepository.save(account);
        aircraftOperatorRepository.save(aircraftOperator);
        Assert.assertTrue(aircraftOperatorRepository.count() > 0);

    }
    @Test
    @Transactional
    public void testSpringData() {
        Assert.assertNotNull(userRepository);

        User user = new User();
        user.setUrid("GR12345");

        userRepository.save(user);

        User user1 = userRepository.findByUrid(user.getUrid());
        Assert.assertEquals(user, user1);
    }

}
