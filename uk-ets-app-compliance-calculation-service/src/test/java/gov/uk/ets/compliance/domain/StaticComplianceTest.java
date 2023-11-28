package gov.uk.ets.compliance.domain;

import gov.uk.ets.compliance.domain.events.CompliantEntityInitializationEvent;
import gov.uk.ets.compliance.domain.events.ExclusionEvent;
import gov.uk.ets.compliance.domain.events.base.StaticComplianceRequestEvent;
import gov.uk.ets.compliance.service.DynamicComplianceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class StaticComplianceTest {


    private static final Long TEST_COMPLIANT_ENTITY_ID = 1000153L;
    private static final int TEST_CURRENT_YEAR = 2021;
    private static final int TEST_FYVE = 2021;
    private static final int TEST_LYVE = 2023;


    @Spy
    DynamicCompliance dynamicCompliance;

    @Test
    public void shouldThrowWhenThereAreNoStates() {
        dynamicCompliance = new DynamicCompliance();
        StaticComplianceRequestEvent event = StaticComplianceRequestEvent.builder().build();

        Assertions.assertThrows(DynamicComplianceException.class, () -> dynamicCompliance.processStaticComplianceRequestEvent(event));
    }

    @Test
    public void shouldCutOffEventsAfterTheDateRequested() {
        LocalDateTime accountCreationDateRequested = LocalDateTime.of(2021, 4, 1, 0, 0, 0);

        dynamicCompliance = new DynamicCompliance(TEST_COMPLIANT_ENTITY_ID, TEST_CURRENT_YEAR + 1, TEST_FYVE, TEST_LYVE, accountCreationDateRequested);

        dynamicCompliance.processDynamicComplianceUpdateEvent(CompliantEntityInitializationEvent.builder()
                .compliantEntityId(TEST_COMPLIANT_ENTITY_ID)
                .dateRequested(accountCreationDateRequested)
                .build());

        LocalDateTime exclusionDateRequested = LocalDateTime.of(2021, 4, 1, 0, 0, 2);
        dynamicCompliance.processDynamicComplianceUpdateEvent(ExclusionEvent.builder()
                .compliantEntityId(TEST_COMPLIANT_ENTITY_ID)
                .dateRequested(exclusionDateRequested)
                .year(TEST_CURRENT_YEAR)
                .build());

        LocalDateTime staticStatusDateRequested = LocalDateTime.of(2021, 4, 1, 0, 0, 1);
        StaticComplianceRequestEvent event = StaticComplianceRequestEvent.builder()
                .compliantEntityId(TEST_COMPLIANT_ENTITY_ID)
                .dateRequested(staticStatusDateRequested)
                .build();

        ComplianceState complianceState = dynamicCompliance.processStaticComplianceRequestEvent(event);

        assertThat(complianceState).isNotNull();
        assertThat(complianceState.getDynamicStatus()).isEqualTo(ComplianceStatus.C);

    }

    @Test
    public void shouldIncludeEventsBeforeTheDateRequested() {
        LocalDateTime accountCreationDateRequested = LocalDateTime.of(2021, 4, 1, 0, 0, 0);

        dynamicCompliance = new DynamicCompliance(TEST_COMPLIANT_ENTITY_ID, TEST_CURRENT_YEAR + 1, TEST_FYVE, TEST_LYVE, accountCreationDateRequested);

        dynamicCompliance.processDynamicComplianceUpdateEvent(CompliantEntityInitializationEvent.builder()
                .compliantEntityId(TEST_COMPLIANT_ENTITY_ID)
                .dateRequested(accountCreationDateRequested.plusSeconds(1))
                .build());

        LocalDateTime exclusionDateRequested = LocalDateTime.of(2021, 4, 1, 0, 0, 2);
        dynamicCompliance.processDynamicComplianceUpdateEvent(ExclusionEvent.builder()
                .compliantEntityId(TEST_COMPLIANT_ENTITY_ID)
                .dateRequested(exclusionDateRequested)
                .year(TEST_CURRENT_YEAR)
                .build());

        LocalDateTime staticStatusDateRequested = LocalDateTime.of(2021, 4, 1, 0, 0, 3);
        StaticComplianceRequestEvent event = StaticComplianceRequestEvent.builder()
                .compliantEntityId(TEST_COMPLIANT_ENTITY_ID)
                .dateRequested(staticStatusDateRequested)
                .build();

        ComplianceState complianceState = dynamicCompliance.processStaticComplianceRequestEvent(event);

        assertThat(complianceState).isNotNull();
        assertThat(complianceState.getDynamicStatus()).isEqualTo(ComplianceStatus.EXCLUDED);

    }

}
