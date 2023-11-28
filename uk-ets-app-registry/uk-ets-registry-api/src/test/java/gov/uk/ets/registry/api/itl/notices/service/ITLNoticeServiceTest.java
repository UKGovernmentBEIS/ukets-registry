package gov.uk.ets.registry.api.itl.notices.service;

import gov.uk.ets.registry.api.itl.notice.ITLNoticeService;
import gov.uk.ets.registry.api.itl.notice.repository.NoticeLogHistoryRepository;
import gov.uk.ets.registry.api.itl.notice.repository.NoticeLogRepository;
import gov.uk.ets.registry.api.itl.notice.repository.NoticeUnitBlockRepository;
import gov.uk.ets.registry.api.transaction.repository.AccountHoldingRepository;
import gov.uk.ets.registry.api.transaction.repository.UnitBlockRepository;
import gov.uk.ets.registry.api.transaction.service.PredefinedAcquiringAccountsProperties;
import gov.uk.ets.registry.api.transaction.service.ProjectService;
import gov.uk.ets.registry.api.transaction.service.TransactionPersistenceService;
import uk.gov.ets.lib.commons.kyoto.types.ITLNoticeRequest;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create"
})
class ITLNoticeServiceTest {

    @Autowired
    private NoticeLogHistoryRepository noticeLogHistoryRepository;

    @Autowired
    private NoticeLogRepository noticeLogRepository;

    @Autowired
    private NoticeUnitBlockRepository noticeUnitBlockRepository;

    private UnitBlockRepository unitBlockRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private ITLNoticeService itlNoticeService;

    private ITLNoticeRequest itlNoticeRequest;

    private PredefinedAcquiringAccountsProperties predefinedAcquiringAccountsProperties;

    private TransactionPersistenceService transactionPersistenceService;

    private AccountHoldingRepository accountHoldingRepository;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        itlNoticeService = new ITLNoticeService(noticeLogHistoryRepository,
                noticeLogRepository, noticeUnitBlockRepository,unitBlockRepository, predefinedAcquiringAccountsProperties,
                transactionPersistenceService, accountHoldingRepository, projectService);

        itlNoticeRequest = new ITLNoticeRequest();
        itlNoticeRequest.setActionDueDate(new Date());
        itlNoticeRequest.setUnitType(5);
        itlNoticeRequest.setLULUCFActivity(2L);
        itlNoticeRequest.setTargetDate(new Date());
        itlNoticeRequest.setNotificationType(2);
        itlNoticeRequest.setNotificationStatus(5);
        itlNoticeRequest.setProjectNumber("1");
        itlNoticeRequest.setCommitPeriod(2);
        itlNoticeRequest.setMessageDate(Calendar.getInstance());
    }

    @Test
    void testDbInsertion() throws InterruptedException {
        itlNoticeRequest.setNotificationIdentifier(1);
        itlNoticeService.processIncomingNotice(itlNoticeRequest);
        itlNoticeRequest.setNotificationIdentifier(2);
        itlNoticeService.processIncomingNotice(itlNoticeRequest);

        List dbItems = entityManager.createNativeQuery("SELECT * FROM itl_notification where identifier in (:low_value,:high_value)")
                .setParameter("low_value", 1)
                .setParameter("high_value", 2)
                .getResultList();

        Assert.assertEquals(2, dbItems.size());
    }

    @Test
    void serviceDoesNotInsertWhenNotificationIdentifierAlreadyExists() {
        itlNoticeRequest.setNotificationIdentifier(1);
        itlNoticeService.processIncomingNotice(itlNoticeRequest);
        itlNoticeRequest.setNotificationIdentifier(1);
        itlNoticeService.processIncomingNotice(itlNoticeRequest);

        List dbItems = entityManager.createNativeQuery("SELECT * FROM itl_notification where identifier in (:low_value,:high_value)")
                .setParameter("low_value", 1)
                .setParameter("high_value", 2)
                .getResultList();

        Assert.assertEquals(1, dbItems.size());
    }

}
