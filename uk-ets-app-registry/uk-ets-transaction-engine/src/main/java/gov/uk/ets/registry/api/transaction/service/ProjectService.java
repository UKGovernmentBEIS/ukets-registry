package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.ProjectRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * Service for joint implementation (JI) projects.
 */
@Service
@AllArgsConstructor
public class ProjectService {

    /**
     * Repository for projects.
     */
    private ProjectRepository projectRepository;

    /**
     * Retrieves the project numbers of the provided account.
     * @param accountIdentifier The account identifier.
     * @param unitType The unit type.
     * @param commitmentPeriod The commitment period.
     * @return some project numbers.
     */
    public List<String> retrieveProjects(Long accountIdentifier, UnitType unitType, CommitmentPeriod commitmentPeriod) {
        List<String> result;
        if (commitmentPeriod == null) {
            result = projectRepository.getProjects(accountIdentifier, unitType);

        } else {
            result = projectRepository.getProjects(accountIdentifier, unitType, commitmentPeriod);
        }
        return result;
    }

    /**
     * Retrieves the environmental activities of the provided account..
     * @param accountIdentifier The account identifier.
     * @param unitType The unit type.
     * @param commitmentPeriod The commitment period.
     * @return some project numbers.
     */
    public List<EnvironmentalActivity> retrieveEnvironmentalActivities(Long accountIdentifier, UnitType unitType, CommitmentPeriod commitmentPeriod) {
        List<EnvironmentalActivity> result;
        if (commitmentPeriod == null) {
            result = projectRepository.getEnvironmentalActivities(accountIdentifier, unitType);

        } else {
            result = projectRepository.getEnvironmentalActivities(accountIdentifier, unitType, commitmentPeriod);
        }
        return result;
    }

    /**
     * Extracts the identifier from the provided project number.
     * @param projectNumber The project number.
     * @return an identifier
     */
    public Integer extractProjectIdentifier(String projectNumber) {
        Integer result = null;
        ImmutablePair<String, Integer> project = parseProjectNumber(projectNumber);
        if (project != null) {
            result = project.getRight();
        }
        return result;
    }

    /**
     * Extracts the party from the provided project number.
     * @param projectNumber The project number.
     * @return the registry
     */
    public String extractProjectParty(String projectNumber) {
        String result = null;
        ImmutablePair<String, Integer> project = parseProjectNumber(projectNumber);
        if (project != null) {
            result = project.getLeft();
        }
        return result;
    }

    /**
     * Parses the provided project number.
     * @param projectNumber The project number.
     * @return an array with the project registry code and the identifier.
     */
    private ImmutablePair<String, Integer> parseProjectNumber(String projectNumber) {
        if (ObjectUtils.isEmpty(projectNumber) || projectNumber.length() < 3) {
            return null;
        }
        String registry = projectNumber.substring(0, 2);
        String identifier = projectNumber.substring(2);

        if (!Utils.isLong(identifier)) {
            throw new IllegalArgumentException(String.format("The project identifier %s is invalid", identifier));
        }

        return new ImmutablePair<>(registry, Integer.valueOf(identifier));
    }

}
