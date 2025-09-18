package gov.uk.ets.registry.api.itl.notices.repository;

import gov.uk.ets.registry.api.itl.notice.domain.ITLNotification;
import gov.uk.ets.registry.api.itl.notice.domain.ITLNotificationBlock;
import gov.uk.ets.registry.api.itl.notice.repository.NoticeUnitBlockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create"})
class ITLNoticeIdentifierRepositoryTest {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private NoticeUnitBlockRepository noticeUnitBlockRepository;

    @Test
    void findByMessageId() {
        ITLNotification noticeLog = new ITLNotification();
        noticeLog.setNotificationIdentifier(9L);

        ITLNotificationBlock noticeUnitBlock1 = new ITLNotificationBlock();
        noticeUnitBlock1.setOriginatingRegistryCode("342");
        noticeUnitBlock1.setUnitSerialBlockStart(6004L);
        noticeUnitBlock1.setUnitSerialBlockEnd(10404L);
        noticeUnitBlock1.setNotification(noticeLog);

        ITLNotificationBlock noticeUnitBlock2 = new ITLNotificationBlock();
        noticeUnitBlock2.setOriginatingRegistryCode("343");
        noticeUnitBlock2.setUnitSerialBlockStart(20004L);
        noticeUnitBlock2.setUnitSerialBlockEnd(20005L);
        noticeUnitBlock2.setNotification(noticeLog);

        Set<ITLNotificationBlock> noticeUnitBlocks = new HashSet<>();
        noticeUnitBlocks.add(noticeUnitBlock1);
        noticeUnitBlocks.add(noticeUnitBlock2);
        noticeLog.setUnitBlockIdentifiers(noticeUnitBlocks);

        entityManager.persist(noticeLog);
        entityManager.persist(noticeUnitBlock1);
        entityManager.persist(noticeUnitBlock2);

        Set<ITLNotificationBlock> noticeBlocks = noticeUnitBlockRepository.findAllNoticeUnitBlocksOfNotificationIdentifier(9L);
        assertNotNull(noticeBlocks);
        assertEquals(2, noticeBlocks.size());
        List<ITLNotificationBlock> noticeUnitBlockList = new ArrayList<>(noticeBlocks);
        assertEquals(noticeLog.getNotificationIdentifier(), noticeUnitBlockList.get(0).getNotification().getNotificationIdentifier());
        assertEquals(noticeLog.getNotificationIdentifier(), noticeUnitBlockList.get(1).getNotification().getNotificationIdentifier());


        Set<ITLNotificationBlock> noticeUnitBlocksEmpty = noticeUnitBlockRepository.findAllNoticeUnitBlocksOfNotificationIdentifier(11L);
        assertTrue(noticeUnitBlocksEmpty == null || noticeUnitBlocksEmpty.isEmpty());
    }
}
