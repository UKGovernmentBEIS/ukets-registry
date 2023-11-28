package gov.uk.ets.registry.api.user.migration;

import static gov.uk.ets.registry.api.migration.domain.MigratorName.COUNTRY_ATTRIBUTES_MIGRATOR;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;


@PostgresJpaTest
@Import({CountryAttributesMigrator.class})
public class CountryAttributesMigratorTest {
    

	private static final String KEYCLOACK_ATTRIBUTE_COUNTRY = "country";
	private static final String KEYCLOACK_ATTRIBUTE_WORK_COUNTRY = "workCountry";
	private static final String KEYCLOACK_ATTRIBUTE_COUNTRY_OF_BIRTH = "countryOfBirth";
	private static final String GB = "GB";

    @MockBean
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    
    @Autowired
    CountryAttributesMigrator migrator;
    
    @Autowired
    MigratorHistoryRepository migratorHistoryRepository;
    
    @Test
    void shouldMigrateCountryAttribute() {
    	
    	UserRepresentation mockUser1 = new UserRepresentation();
    	mockUser1.setId("1");
    	mockUser1.setAttributes(new HashMap<>());
    	mockUser1.getAttributes().put(KEYCLOACK_ATTRIBUTE_COUNTRY, List.of(GB));
    	mockUser1.getAttributes().put(KEYCLOACK_ATTRIBUTE_COUNTRY_OF_BIRTH, List.of(GB));
    	
    	UserRepresentation mockUser2 = new UserRepresentation();
    	mockUser2.setId("2");
    	mockUser2.setAttributes(new HashMap<>());
    	mockUser2.getAttributes().put(KEYCLOACK_ATTRIBUTE_COUNTRY, List.of(GB));
    	
    	UserRepresentation mockUser3 = new UserRepresentation();
    	mockUser3.setId("3");
    	mockUser3.setAttributes(new HashMap<>());
    	mockUser3.getAttributes().put(KEYCLOACK_ATTRIBUTE_COUNTRY_OF_BIRTH, List.of(GB));
    	
    	UserRepresentation mockUser4 = new UserRepresentation();
    	mockUser4.setId("4");
    	mockUser4.setAttributes(new HashMap<>());
    	mockUser4.getAttributes().put(KEYCLOACK_ATTRIBUTE_WORK_COUNTRY, List.of(GB));

		when(serviceAccountAuthorizationService.searchBySingleAttributes(singletonMap(KEYCLOACK_ATTRIBUTE_COUNTRY, GB))).thenReturn(List.of(mockUser1, mockUser2));
		when(serviceAccountAuthorizationService.searchBySingleAttributes(singletonMap(KEYCLOACK_ATTRIBUTE_COUNTRY_OF_BIRTH, GB))).thenReturn(List.of(mockUser3));
		when(serviceAccountAuthorizationService.searchBySingleAttributes(singletonMap(KEYCLOACK_ATTRIBUTE_WORK_COUNTRY, GB))).thenReturn(List.of(mockUser4));

		migrator.migrate();
		
		verify(serviceAccountAuthorizationService, times(4)).updateUserDetails(any());
		
		List<MigratorHistory> migratorHistoryList = migratorHistoryRepository.findByMigratorName(COUNTRY_ATTRIBUTES_MIGRATOR);
		assertTrue(CollectionUtils.isNotEmpty(migratorHistoryList));
    }
    
	@Test
	void shouldSkipMigrationIfAlreadyExecuted() {
		updateMigrationHistory();
		
		migrator.migrate();

		verify(serviceAccountAuthorizationService, times(0)).searchBySingleAttributes(any());
	}
    
	@Test
	void shouldSkipMigrationIfNoDataFound() {

		when(serviceAccountAuthorizationService.searchBySingleAttributes(singletonMap(KEYCLOACK_ATTRIBUTE_COUNTRY, GB))).thenReturn(emptyList());
		when(serviceAccountAuthorizationService.searchBySingleAttributes(singletonMap(KEYCLOACK_ATTRIBUTE_COUNTRY_OF_BIRTH, GB))).thenReturn(emptyList());
		when(serviceAccountAuthorizationService.searchBySingleAttributes(singletonMap(KEYCLOACK_ATTRIBUTE_WORK_COUNTRY, GB))).thenReturn(emptyList());

		migrator.migrate();

		verify(serviceAccountAuthorizationService, times(0)).updateUserDetails(any());
		
		List<MigratorHistory> migratorHistoryList = migratorHistoryRepository.findByMigratorName(COUNTRY_ATTRIBUTES_MIGRATOR);
		assertTrue(CollectionUtils.isNotEmpty(migratorHistoryList));

	}

	private void updateMigrationHistory() {
		MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(COUNTRY_ATTRIBUTES_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
	}
}
