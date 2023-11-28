package gov.uk.ets.registry.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import gov.uk.ets.registry.api.transaction.CommonTransactionObjectsBuilder;
import gov.uk.ets.registry.api.transaction.domain.AccountBasicInfo;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.ProjectRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionBlockRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionHistoryRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.api.transaction.repository.UnitBlockRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create"
})
public class TransactionModelTests {

    @Autowired
    private UnitBlockRepository unitBlockRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private TransactionBlockRepository transactionBlockRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @Transactional
    public void testProjectRepository() {
        assert projectRepository != null;

        List<UnitBlock> units = new ArrayList<>() {{
            add(CommonTransactionObjectsBuilder.uniBlock("GB", 1L, UnitType.AAU, CommitmentPeriod.CP2, 100L, 109L, null, null));
            add(CommonTransactionObjectsBuilder.uniBlock("GB", 1L, UnitType.AAU, CommitmentPeriod.CP2, 10000L, 10009L, null, null));
            add(CommonTransactionObjectsBuilder.uniBlock("JP", 1L, UnitType.CER, CommitmentPeriod.CP1, 12L, 14L, "JP123", null));
            add(CommonTransactionObjectsBuilder.uniBlock("KR", 1L, UnitType.CER, CommitmentPeriod.CP2, 120L, 121L, "KR555", null));
            add(CommonTransactionObjectsBuilder.uniBlock("JP", 1L, UnitType.CER, CommitmentPeriod.CP1, 16L, 18L, "JP123", null));
            add(CommonTransactionObjectsBuilder.uniBlock("BO", 1L, UnitType.CER, CommitmentPeriod.CP1, 16000L, 16008L, "BO111", null));
            add(CommonTransactionObjectsBuilder.uniBlock("KR", 1L, UnitType.CER, CommitmentPeriod.CP2, 128L, 129L, "KR555", null));
            add(CommonTransactionObjectsBuilder.uniBlock("FR", 1L, UnitType.RMU, CommitmentPeriod.CP2, 1L, 2L, null, EnvironmentalActivity.AFFORESTATION_AND_REFORESTATION));
            add(CommonTransactionObjectsBuilder.uniBlock("FR", 1L, UnitType.RMU, CommitmentPeriod.CP2, 4L, 5L, null, EnvironmentalActivity.AFFORESTATION_AND_REFORESTATION));
            add(CommonTransactionObjectsBuilder.uniBlock("FR", 1L, UnitType.RMU, CommitmentPeriod.CP2, 1324234L, 1324235L, null, EnvironmentalActivity.GRAZING_LAND_MANAGEMENT));
            add(CommonTransactionObjectsBuilder.uniBlock("FR", 1L, UnitType.RMU, CommitmentPeriod.CP1, 777L, 780L, null, EnvironmentalActivity.DEFORESTATION));
        }};

        unitBlockRepository.saveAll(units);

        Assert.assertTrue(projectRepository.getProjects(1L, UnitType.CER, CommitmentPeriod.CP1).size() == 2);
        Assert.assertTrue(projectRepository.getProjects(1L, UnitType.CER, CommitmentPeriod.CP2).size() == 1);
        Assert.assertTrue(projectRepository.getProjects(1L, UnitType.CER).size() == 3);
        Assert.assertTrue(projectRepository.getEnvironmentalActivities(1L, UnitType.RMU).size() == 3);
        Assert.assertTrue(projectRepository.getEnvironmentalActivities(1L, UnitType.RMU, CommitmentPeriod.CP2).size() == 2);
        Assert.assertTrue(projectRepository.getEnvironmentalActivities(1L, UnitType.RMU, CommitmentPeriod.CP1).size() == 1);

    }

    @Test
    public void initialisedSuccessfully() {
        assertNotNull(unitBlockRepository);
        assertNotNull(transactionRepository);
        assertNotNull(transactionBlockRepository);
        assertNotNull(transactionHistoryRepository);
    }

    @Test
    @Transactional
    public void saveUnitBlock() {
        UnitBlock block = new UnitBlock();
        block.setAccountIdentifier(100L);
        block.setAcquisitionDate(new Date());
        block.setApplicablePeriod(CommitmentPeriod.getCurrentPeriod());
        block.setOriginalPeriod(CommitmentPeriod.getCurrentPeriod());
        block.setStartBlock(100000000000000L);
        block.setEndBlock(100000000010000L);
        block.setType(UnitType.AAU);
        unitBlockRepository.save(block);

        List<UnitBlock> list = unitBlockRepository.findAll();
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    @Transactional
    public void saveTransaction() {
        Transaction transaction = new Transaction();
        transaction.setIdentifier("GB12345");
        transaction.setQuantity(20L);
        transaction.setStatus(TransactionStatus.PROPOSED);
        transaction.setType(TransactionType.IssueOfAAUsAndRMUs);

        AccountBasicInfo transferringAccountBasicInfo = new AccountBasicInfo();
        transferringAccountBasicInfo.setAccountFullIdentifier("GB-100-10001-0-44");
        transferringAccountBasicInfo.setAccountIdentifier(10001L);
        transferringAccountBasicInfo.setAccountRegistryCode("GB");
        transferringAccountBasicInfo.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);

        transaction.setTransferringAccount(transferringAccountBasicInfo);

        AccountBasicInfo acquiringAccountBasicInfo = new AccountBasicInfo();
        acquiringAccountBasicInfo.setAccountFullIdentifier("JP-100-700900-0");
        acquiringAccountBasicInfo.setAccountIdentifier(700900L);
        acquiringAccountBasicInfo.setAccountRegistryCode("JP");
        acquiringAccountBasicInfo.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);

        transaction.setAcquiringAccount(acquiringAccountBasicInfo);

        transactionRepository.save(transaction);

        TransactionBlock block = new TransactionBlock();
        block.setApplicablePeriod(CommitmentPeriod.getCurrentPeriod());
        block.setOriginalPeriod(CommitmentPeriod.getCurrentPeriod());
        block.setStartBlock(10000000000L);
        block.setEndBlock(10000000050L);
        block.setType(UnitType.RMU);
        block.setEnvironmentalActivity(EnvironmentalActivity.CROPLAND_MANAGEMENT);
        block.setTransaction(transaction);
        transactionBlockRepository.save(block);

        block = new TransactionBlock();
        block.setApplicablePeriod(CommitmentPeriod.getCurrentPeriod());
        block.setOriginalPeriod(CommitmentPeriod.getCurrentPeriod());
        block.setStartBlock(10000000000L);
        block.setEndBlock(10000000050L);
        block.setType(UnitType.ERU_FROM_AAU);
        block.setEnvironmentalActivity(EnvironmentalActivity.CROPLAND_MANAGEMENT);
        block.setTransaction(transaction);
        transactionBlockRepository.save(block);

        List<TransactionBlock> list = transactionBlockRepository.findAll();
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertEquals(2, list.size());
    }

}
