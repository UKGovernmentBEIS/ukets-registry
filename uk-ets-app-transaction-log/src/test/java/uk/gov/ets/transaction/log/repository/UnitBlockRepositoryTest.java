package uk.gov.ets.transaction.log.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import uk.gov.ets.transaction.log.domain.UnitBlock;
import uk.gov.ets.transaction.log.domain.type.UnitType;
import uk.gov.ets.transaction.log.helper.BaseJpaTest;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationEntrySummary;

//Added this property spring.jpa.properties.hibernate.globally_quoted_identifiers=true 
//when upgrading spring-boot from 2.5.6 to 2.7.10 due to
//h2 database dependency upgrade.The root cause of the problem is the use
//of the SQL reserved keyword year in entity UnitBlock and hence in the generated SQL query
@TestPropertySource(properties = {"spring.jpa.properties.hibernate.globally_quoted_identifiers=true"})
public class UnitBlockRepositoryTest extends BaseJpaTest {

    public static final long ACCOUNT_IDENTIFIER_1 = 1009L;
    public static final long ACCOUNT_IDENTIFIER_2 = 1010L;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UnitBlockRepository unitBlockRepository;

    @BeforeEach
    public void setUp() {
        UnitBlock block = new UnitBlock();
        block.setStartBlock(9L);
        block.setEndBlock(22L);
        block.setAccountIdentifier(ACCOUNT_IDENTIFIER_1);
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(2020, 7, 8);
        block.setAcquisitionDate(cal.getTime());
        block.setLastModifiedDate(cal.getTime());
        block.setType(UnitType.ALLOWANCE);
        block.setYear(2020);
        entityManager.persist(block);
    }

    @Test
    @DisplayName("findByAccountIdentifier Blocks are fetched correctly.")
    public void findByAccountIdentifier() {
        List<UnitBlock> blocks = unitBlockRepository.findByAccountIdentifier(ACCOUNT_IDENTIFIER_1);
        assertEquals(1L, blocks.size());
    }

    @Test
    @DisplayName("Exactly Overlapping Blocks are fetched correctly.")
    public void findExactlyOverlappingBlocks() {
        List<UnitBlock> blocks =
            unitBlockRepository.findOverlappingBlocksByEndBlockGreaterThanEqualAndStartBlockLessThanEqual(9L, 22L);
        assertEquals(1L, blocks.size());
    }

    @Test
    @DisplayName("Overlapping Blocks are fetched correctly.")
    public void findOverlappingBlocks() {
        List<UnitBlock> blocks =
            unitBlockRepository.findOverlappingBlocksByEndBlockGreaterThanEqualAndStartBlockLessThanEqual(5L, 212L);
        assertEquals(1L, blocks.size());

        blocks =
            unitBlockRepository.findOverlappingBlocksByEndBlockGreaterThanEqualAndStartBlockLessThanEqual(12L, 15L);
        assertEquals(1L, blocks.size());
    }

    @Test
    @DisplayName("Overlapping Blocks End Block are fetched correctly.")
    public void findEndBlockOverlappingBlocks() {
        List<UnitBlock> blocks =
            unitBlockRepository.findOverlappingBlocksByEndBlockGreaterThanEqualAndStartBlockLessThanEqual(22L, 123L);
        assertEquals(1L, blocks.size());
    }

    @Test
    @DisplayName("Overlapping Blocks Start Block are fetched correctly.")
    public void findStartBlockOverlappingBlocks() {
        List<UnitBlock> blocks =
            unitBlockRepository.findOverlappingBlocksByEndBlockGreaterThanEqualAndStartBlockLessThanEqual(2L, 9L);
        assertEquals(1L, blocks.size());
    }

    @Test
    @DisplayName("Non Overlapping Blocks are not fetched.")
    public void findNonOverlappingBlocks() {
        List<UnitBlock> blocks =
            unitBlockRepository.findOverlappingBlocksByEndBlockGreaterThanEqualAndStartBlockLessThanEqual(2L, 8L);
        assertEquals(0L, blocks.size());

        blocks =
            unitBlockRepository.findOverlappingBlocksByEndBlockGreaterThanEqualAndStartBlockLessThanEqual(23L, 84L);
        assertEquals(0L, blocks.size());
    }

    @Test
    @DisplayName("findIncludedBlocks Blocks are fetched correctly.")
    public void findIncludedBlocks() {
        List<UnitBlock> blocks =
            unitBlockRepository.findIncludedBlocksByStartBlockGreaterThanEqualAndEndBlockLessThanEqual(9L, 22L);
        assertEquals(1L, blocks.size());
    }

    @Test
    @DisplayName("Overlapping Blocks starts too early.")
    public void overlappingBlocksStartsEarly() {
        UnitBlock block_1 = new UnitBlock();
        block_1.setStartBlock(10L);
        block_1.setEndBlock(18L);
        UnitBlock block_2 = new UnitBlock();
        block_2.setStartBlock(12L);
        block_2.setEndBlock(19L);
        assertTrue(block_1.isOverlapping(block_2));
    }

    @Test
    @DisplayName("Overlapping Blocks starts too early end blocks equal.")
    public void overlappingBlocksStartsEarlyEndEqual() {
        UnitBlock block_1 = new UnitBlock();
        block_1.setStartBlock(10L);
        block_1.setEndBlock(12L);
        UnitBlock block_2 = new UnitBlock();
        block_2.setStartBlock(12L);
        block_2.setEndBlock(19L);
        assertTrue(block_1.isOverlapping(block_2));
    }

    @Test
    @DisplayName("Overlapping Blocks starts too early end blocks early.")
    public void nonOverlappingBlocksStartsEarlyEndsEarly() {
        UnitBlock block_1 = new UnitBlock();
        block_1.setStartBlock(10L);
        block_1.setEndBlock(11L);
        UnitBlock block_2 = new UnitBlock();
        block_2.setStartBlock(12L);
        block_2.setEndBlock(19L);
        assertFalse(block_1.isOverlapping(block_2));
    }

    @Test
    public void shouldSumUpBlocks() {
        UnitBlock unitBlock1 = createUnitBlockForAccountWithBlocks(ACCOUNT_IDENTIFIER_1, 23L, 40L);
        UnitBlock unitBlock2 = createUnitBlockForAccountWithBlocks(ACCOUNT_IDENTIFIER_2, 40L, 52L);
        entityManager.persistAndFlush(unitBlock1);
        entityManager.persistAndFlush(unitBlock2);

        List<ReconciliationEntrySummary> reconciliationEntries =
            unitBlockRepository.retrieveEntriesForAllAccounts();

        assertThat(reconciliationEntries).hasSize(2);
        assertThat(reconciliationEntries).extracting(ReconciliationEntrySummary::getAccountIdentifier)
            .contains(ACCOUNT_IDENTIFIER_1, ACCOUNT_IDENTIFIER_2);
        assertThat(reconciliationEntries).extracting(ReconciliationEntrySummary::getTotal)
            .containsExactly(32L, 13L);
    }
}
