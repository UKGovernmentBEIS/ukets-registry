package gov.uk.ets.registry.api.notification.userinitiated.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
import gov.uk.ets.registry.api.notification.userinitiated.NotificationModelTestHelper;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationSearchCriteria;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationSearchResult;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@PostgresJpaTest
class NotificationResultsRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private NotificationDefinitionRepository definitionRepository;

    @Autowired
    private NotificationRepository cut;

    private NotificationModelTestHelper notificationModelTestHelper;

    @BeforeEach
    void setUp() {
        notificationModelTestHelper = new NotificationModelTestHelper(entityManager, definitionRepository);

        notificationModelTestHelper.setUp();
    }

    @Test
    void shouldReturnAllNotificationsWhenNoCriteria() {

        Page<NotificationSearchResult> results =
            cut.search(new NotificationSearchCriteria(null), PageRequest.of(0, 10));

        assertThat(results).hasSize(8);
    }


    @Test
    void shouldReturnOnlyAdHoc() {

        Page<NotificationSearchResult> results =
            cut.search(new NotificationSearchCriteria(NotificationType.AD_HOC), PageRequest.of(0, 10));

        assertThat(results).hasSize(7);
        List<NotificationSearchResult> content = results.getContent();

        List<NotificationType> adHocTypes = Collections.nCopies(7, NotificationType.AD_HOC);
        assertThat(content).extracting(NotificationSearchResult::getType).containsAll(adHocTypes);
    }

    @Test
    void shouldReturnOnlyAdHocPaged() {

        Page<NotificationSearchResult> results =
            cut.search(new NotificationSearchCriteria(NotificationType.AD_HOC), PageRequest.of(0, 5));

        assertThat(results).hasSize(5);
    }
}
