package gov.uk.ets.registry.api.auditevent.service;

import gov.uk.ets.registry.api.auditevent.repository.DomainEventEntityRepository;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.task.domain.types.RequestType;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditEventService {
    
    private final DomainEventEntityRepository domainEventEntityRepository;
    
    public List<AuditEventDTO> getAuditEventsByDomainTypeAndRequestTypeOrderByCreationDate(String domainType,RequestType requestType) {
        return domainEventEntityRepository.findByDomainTypeAndRequestTypeWithUserDetails(domainType,requestType);
    }

    public List<AuditEventDTO> getAuditEventsByDomainTypeAndRequestTypesAndOptionalUserDetailsOrderByCreationDate(String domainType, List<RequestType> requestTypes) {
        return domainEventEntityRepository.findByDomainTypeAndRequestTypeWithOptionalUserDetails(domainType, requestTypes);
    }
}
