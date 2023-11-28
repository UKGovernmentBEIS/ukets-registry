package gov.uk.ets.registry.api.transaction;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import gov.uk.ets.registry.api.transaction.domain.RegistryLevel;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.repository.RegistryLevelRepository;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

// TODO: enable tests
@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create"
})

@Sql({"/test-data/test-data.sql"})
public class IssueUnitsServiceTest {

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private RegistryLevelRepository registryLevelRepository;


  @Test
  @Ignore("TODO: enable tests")
  public void testFindAll() {
    List<RegistryLevel> registryLevelList = registryLevelRepository.findAll();
    assertThat(registryLevelList.size(), is(24));
  }


  @Test
  @Ignore("TODO: enable tests")
  public void testGetAccountsForCommitmentPeriod() {

    List<AccountInfo> results1 = registryLevelRepository
        .findAccountByCommitmentPeriod(CommitmentPeriod.CP1.getCode(), KyotoAccountType.PARTY_HOLDING_ACCOUNT,RegistryAccountType.NONE,
                                       List.of(AccountStatus.CLOSURE_PENDING,AccountStatus.CLOSED));
    assertThat(results1.size(), is(3));
    List<AccountInfo> results2 = registryLevelRepository
        .findAccountByCommitmentPeriod(CommitmentPeriod.CP2.getCode(),KyotoAccountType.PARTY_HOLDING_ACCOUNT, RegistryAccountType.NONE,
                                       List.of(AccountStatus.CLOSURE_PENDING,AccountStatus.CLOSED));
    assertThat(results2.size(), is(3));
    List<AccountInfo> results3 = registryLevelRepository
        .findAccountByCommitmentPeriod(CommitmentPeriod.CP3.getCode(),KyotoAccountType.PARTY_HOLDING_ACCOUNT, RegistryAccountType.NONE,
                                       List.of(AccountStatus.CLOSURE_PENDING,AccountStatus.CLOSED));
    assertThat(results3.size(), is(4));
  }

//  @Test
//  public void testGetUnitsTable() {
//    registryLevelRepository.findByPeriodAndType(CommitmentPeriod.CP1,
//        RegistryLevelType.ISSUANCE_KYOTO_LEVEL);
//  }
}
