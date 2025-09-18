package gov.uk.ets.registry.api.compliance.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gov.uk.ets.registry.api.compliance.domain.ExcludeEmissionsEntry;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;



@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true"})
public class ExcludeEmissionsRepositoryTest {

	private static final Long TEST_COMPLIANT_ENTITY_ID_1 = 112233L;
    private static final Long TEST_COMPLIANT_ENTITY_ID_2 = 112234L;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    ExcludeEmissionsRepository excludeEmissionsRepository;
    
    @Test
    public void shouldRetrieveOnlyExcludedEntriesBeforeAndAfterYear() {
        ExcludeEmissionsEntry excludedBeforeYear = new ExcludeEmissionsEntry();
        excludedBeforeYear.setExcluded(true);
        excludedBeforeYear.setCompliantEntityId(TEST_COMPLIANT_ENTITY_ID_1);
        excludedBeforeYear.setYear(2021L);

        ExcludeEmissionsEntry excludedAfterYear = new ExcludeEmissionsEntry();
        excludedAfterYear.setExcluded(true);
        excludedAfterYear.setCompliantEntityId(TEST_COMPLIANT_ENTITY_ID_1);
        excludedAfterYear.setYear(2022L);

        ExcludeEmissionsEntry notExcludedBeforeYear = new ExcludeEmissionsEntry();
        notExcludedBeforeYear.setExcluded(false);
        notExcludedBeforeYear.setCompliantEntityId(TEST_COMPLIANT_ENTITY_ID_1);
        notExcludedBeforeYear.setYear(2021L);
        
        ExcludeEmissionsEntry notExcludedAfterYear = new ExcludeEmissionsEntry();
        notExcludedAfterYear.setExcluded(false);
        notExcludedAfterYear.setCompliantEntityId(TEST_COMPLIANT_ENTITY_ID_1);
        notExcludedAfterYear.setYear(2022L);

        ExcludeEmissionsEntry excludedBeforeYearDifferentCompliantEntityId = new ExcludeEmissionsEntry();
        excludedBeforeYearDifferentCompliantEntityId.setExcluded(true);
        excludedBeforeYearDifferentCompliantEntityId.setCompliantEntityId(TEST_COMPLIANT_ENTITY_ID_2);
        excludedBeforeYearDifferentCompliantEntityId.setYear(2021L);

        ExcludeEmissionsEntry excludedAfterYearDifferentCompliantEntityId = new ExcludeEmissionsEntry();
        excludedAfterYearDifferentCompliantEntityId.setExcluded(true);
        excludedAfterYearDifferentCompliantEntityId.setCompliantEntityId(TEST_COMPLIANT_ENTITY_ID_2);
        excludedAfterYearDifferentCompliantEntityId.setYear(2021L);

        excludeEmissionsRepository.saveAll(List.of(excludedBeforeYear, excludedAfterYear, 
        		notExcludedBeforeYear, notExcludedAfterYear, excludedBeforeYearDifferentCompliantEntityId, 
        		excludedAfterYearDifferentCompliantEntityId));
        excludeEmissionsRepository.flush();

        List<ExcludeEmissionsEntry> entriesBefore = excludeEmissionsRepository.findExcludedEntriesBeforeYear(TEST_COMPLIANT_ENTITY_ID_1, 2022);

        assertThat(entriesBefore).hasSize(1);
        
        List<ExcludeEmissionsEntry> entriesAfter = excludeEmissionsRepository.findExcludedEntriesAfterYear(TEST_COMPLIANT_ENTITY_ID_1, 2021);

        assertThat(entriesAfter).hasSize(1);
    }
}
