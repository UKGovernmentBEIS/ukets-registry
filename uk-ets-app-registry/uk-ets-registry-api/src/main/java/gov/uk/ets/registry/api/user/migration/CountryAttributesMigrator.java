package gov.uk.ets.registry.api.user.migration;

import static gov.uk.ets.registry.api.migration.domain.MigratorName.COUNTRY_ATTRIBUTES_MIGRATOR;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Component
@Log4j2
public class CountryAttributesMigrator implements Migrator {

	private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;
	private final MigratorHistoryRepository migratorHistoryRepository;
	private static final String KEYCLOACK_ATTRIBUTE_COUNTRY = "country";
	private static final String KEYCLOACK_ATTRIBUTE_WORK_COUNTRY = "workCountry";
	private static final String KEYCLOACK_ATTRIBUTE_COUNTRY_OF_BIRTH = "countryOfBirth";
	private static final String GB = "GB";
	private static final String UK = "UK";

	@Transactional
	public void migrate() {
		log.info("Starting migration of the user attributes country, workCountry and countryOfBirth ...");
		List<MigratorHistory> migratorHistoryList = migratorHistoryRepository.findByMigratorName(COUNTRY_ATTRIBUTES_MIGRATOR);
		if (CollectionUtils.isNotEmpty(migratorHistoryList)) {
			log.info("[Country Attributes Migrator], has already been performed, hence, it is skipping it.");
			return;
		}
		
		int countMigratedUsers = 0;

		List<UserRepresentation> migratedUsers = serviceAccountAuthorizationService.searchBySingleAttributes(singletonMap(KEYCLOACK_ATTRIBUTE_COUNTRY, GB));
		if (CollectionUtils.isNotEmpty(migratedUsers)) {
			countMigratedUsers += migratedUsers.size();
			updateCountryAttributes(migratedUsers);
		}
		migratedUsers = serviceAccountAuthorizationService.searchBySingleAttributes(singletonMap(KEYCLOACK_ATTRIBUTE_COUNTRY_OF_BIRTH, GB));
		if (CollectionUtils.isNotEmpty(migratedUsers)) {
			countMigratedUsers = migratedUsers.size();
			updateCountryAttributes(migratedUsers);
		}
		migratedUsers = serviceAccountAuthorizationService.searchBySingleAttributes(singletonMap(KEYCLOACK_ATTRIBUTE_WORK_COUNTRY, GB));
		if (CollectionUtils.isNotEmpty(migratedUsers)) {
			countMigratedUsers += migratedUsers.size();
			updateCountryAttributes(migratedUsers);
		}
		
		log.info("----------- No of users updated: {}", countMigratedUsers);
		
		updateMigrationHistory();
		
		log.info("Refresh of country attributes completed");
	}

	private void updateCountryAttributes(List<UserRepresentation> migratedUsers) {
		migratedUsers.forEach(keycloakUser -> {
			log.debug("----------- KEYCLOAK USER: {}", keycloakUser.getId());
			try {
				Map<String, List<String>> keycloakUserAttributes = keycloakUser.getAttributes();
				setAttributeValueToUKIfGB(KEYCLOACK_ATTRIBUTE_COUNTRY, keycloakUserAttributes);
				setAttributeValueToUKIfGB(KEYCLOACK_ATTRIBUTE_COUNTRY_OF_BIRTH, keycloakUserAttributes);
				setAttributeValueToUKIfGB(KEYCLOACK_ATTRIBUTE_WORK_COUNTRY, keycloakUserAttributes);
				serviceAccountAuthorizationService.updateUserDetails(keycloakUser);

			} catch (Exception e) {
				log.warn("Failed to update the country attributes for keycloakUser with id: {}, skipping migration ", keycloakUser.getId());
			}
		});
	}

	private void setAttributeValueToUKIfGB(String attributeKey, Map<String, List<String>> keycloakUserAttributes) {
		keycloakUserAttributes.computeIfPresent(attributeKey,
				(key, oldValue) -> oldValue == null || !singletonList(GB).equals(oldValue) ? oldValue : singletonList(UK));
	}
	
	private void updateMigrationHistory() {
		MigratorHistory migratorHistory = new MigratorHistory();
		migratorHistory.setMigratorName(COUNTRY_ATTRIBUTES_MIGRATOR);
		migratorHistory.setCreatedOn(LocalDateTime.now());
		migratorHistoryRepository.save(migratorHistory);
	}

}
