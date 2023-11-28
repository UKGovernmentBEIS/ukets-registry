package gov.uk.ets.registry.api.auditevent.repository;

import gov.uk.ets.registry.api.auditevent.domain.DomainEventEntity;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.task.domain.types.RequestType;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DomainEventEntityRepository extends JpaRepository<DomainEventEntity, Long>, AuditEventDTORepository {

    @Query("SELECT new gov.uk.ets.registry.api.auditevent.web.AuditEventDTO( " +
        "de.domainId, " +
        "de.domainAction, " +
        "de.description, " +
        "concat(u.firstName, ' ', u.lastName)," +
        "u.urid, " +
        "de.creatorType, " +
        "de.creationDate) " +
        "from DomainEventEntity de " +
        "inner join Task t on cast (t.requestId as string) = de.domainId " +
        "inner join User u on u.urid = de.creator " +
        "where de.domainType = ?1 " +
        "and t.type = ?2 " +
        "order by de.creationDate desc")
    List<AuditEventDTO> findByDomainTypeAndRequestTypeWithUserDetails(String domainType,RequestType requestType);

    @Query("SELECT new gov.uk.ets.registry.api.auditevent.web.AuditEventDTO( " +
        "de.domainId, " +
        "de.domainAction, " +
        "de.description, " +
        "concat(u.firstName, ' ', u.lastName)," +
        "u.urid, " +
        "de.creatorType, " +
        "de.creationDate) " +
        "from DomainEventEntity de " +
        "inner join Task t on cast (t.requestId as string) = de.domainId " +
        "left join User u on u.urid = de.creator " +
        "where de.domainType = ?1 " +
        "and t.type in ?2 " +
        "order by de.creationDate desc")
    List<AuditEventDTO> findByDomainTypeAndRequestTypeWithOptionalUserDetails(String domainType, List<RequestType> requestTypes);


    @Query("SELECT new gov.uk.ets.registry.api.auditevent.web.AuditEventDTO( " +
            "de.domainId, " +
            "de.domainAction, " +
            "de.description, " +
            "de.creatorType, " +
            "de.creationDate) " +
            "from DomainEventEntity de " +
            "where de.domainId = ?1 " +
            "and de.domainAction in ?2 " +
            "order by de.creationDate desc")
    List<AuditEventDTO> findByDomainIdAndDomainActionDesc(String domainId, List<String> domainActions, Pageable pageable);

    @Query("SELECT count(de) FROM DomainEventEntity de WHERE de.creator = :urId and de.creationDate > :dateOfLastSuccessfulLogin and de.domainAction = :domainAction")
    long findAllByCreatorAndCreationDateAfterAndDomainAction(@Param("urId") String urId,
                                                             @Param("dateOfLastSuccessfulLogin")
                                                                 Timestamp dateOfLastSuccessfulLogin,
                                                             @Param("domainAction") String domainAction);

    @Query("SELECT count(de) FROM DomainEventEntity de WHERE de.creator = :urId and de.domainAction = :domainAction")
    long findAllByCreatorAndDomainAction(@Param("urId") String urId, @Param("domainAction") String domainAction);

}
