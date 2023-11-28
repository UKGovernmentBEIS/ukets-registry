package gov.uk.ets.registry.api.auditevent.repository;

import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import java.util.List;

public interface AuditEventDTORepository {

    /**
     * Finds the domain events for the specified domainId.
     *
     * @param domainId      the business identifier of the domain entity
     * @param isAdmin       determine if is admin
     * @param domainTypes    the requested domain types i.e Task, Account etc
     * @return A list of {@link AuditEventDTO} objects.
     */
    List<AuditEventDTO> getAuditEventsByDomainIdOrderByCreationDateDesc(String domainId, boolean isAdmin, List<String> domainTypes);

    /**
     * Finds the system domain events for the specified domainId.
     * @param domainId the business identifier of the domain entity
     * @param domainTypes    the requested domain types i.e Task, Account etc
     * @return a list of {@link AuditEventDTO} objects
     */
    List<AuditEventDTO> getSystemEventsByDomainIdOrderByCreationDateDesc(String domainId, List<String> domainTypes);
    
    /**
     * Finds the domain events for the specified user id.
     *
     * @param urid the identifier of the user entity
     * @return A list of {@link AuditEventDTO} objects.
     */
    List<AuditEventDTO> getAuditEventsByCreatorOrderByCreationDateDesc(String urid);
}
