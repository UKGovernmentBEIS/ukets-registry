package gov.uk.ets.registry.api.account.repository;

import java.util.List;
import java.util.Optional;

import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.SubmitEmissionsValidityInfo;

/**
 * Data repository for compliant entities.
 */
public interface CompliantEntityRepository
        extends
            JpaRepository<CompliantEntity, Long> {

	/**
	 * Retrieves the next business identifier value.
	 *
	 * @return the next business identifier value.
	 */
	@Query(value = "select nextval('compliant_entity_identifier_seq')", nativeQuery = true)
	Long getNextIdentifier();

	/**
	 * Retrieves the installation / aircraft operator, if exists, of the
	 * provided account.
	 *
	 * @param accountIdentifier
	 *            The account business identifier.
	 * @return a compliant entity
	 */
	@SuppressWarnings("java:S100")
	CompliantEntity findByAccount_Identifier(Long accountIdentifier);

	/**
	 * Retrieves the compliant entity for the provided identifier.
	 *
	 * @param identifier
	 *            The compliant entity identifier.
	 * @return a compliant entity
	 */
	Optional<CompliantEntity> findByIdentifier(Long identifier);

	/**
	 * Retrieves all compliant entities for the provided regulator
	 *
	 * @param regulator
	 * @return list of compliant entities
	 */
	List<CompliantEntity> findByRegulator(RegulatorType regulator);
	
	List<CompliantEntity> findByChangedRegulator(RegulatorType regulator);
	/**
	 * Find all compliant entities together with their account status and first/last emission years.
	 * @return a List of SubmitEmissionsValidityChecker.
	 */
	@Query("select new gov.uk.ets.registry.api.file.upload.emissionstable.model.SubmitEmissionsValidityInfo(c.identifier,a.accountStatus,a.complianceStatus,c.startYear,c.endYear) from Account a right join a.compliantEntity c")
	List<SubmitEmissionsValidityInfo> findAllIdentifiersFetchAccountStatusAndYears();
}
