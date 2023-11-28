package gov.uk.ets.registry.api.itl.notices.repository;

import gov.uk.ets.registry.api.itl.notice.domain.ITLNotification;
import gov.uk.ets.registry.api.itl.notice.repository.NoticeLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create"})
class ITLITLNotificationRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private NoticeLogRepository noticeLogRepository;


    @Test
    void findByMessageId() {

        Stream.of(1L, 2L, 3L, 4L, 5L).forEach(id -> {
            ITLNotification noticeLog = new ITLNotification();
            noticeLog.setNotificationIdentifier(id);

            entityManager.persist(noticeLog);
        });

        Stream.of(1L, 2L, 3L, 4L, 5L).forEach(id -> {
            ITLNotification noticeLog = noticeLogRepository.findNoticeLogsByNotificationIdentifier(id);
            assertNotNull(noticeLog);
            assertEquals(id, noticeLog.getNotificationIdentifier());
        });

        ITLNotification noticeEmpty = noticeLogRepository.findNoticeLogsByNotificationIdentifier(11L);
        assertNull(noticeEmpty);
    }

}
