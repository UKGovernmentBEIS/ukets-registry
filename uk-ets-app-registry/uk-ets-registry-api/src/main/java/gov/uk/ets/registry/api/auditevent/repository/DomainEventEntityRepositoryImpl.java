package gov.uk.ets.registry.api.auditevent.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.auditevent.domain.QDomainEventEntity;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.auditevent.web.QAuditEventDTO;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.user.domain.QIamUserRole;
import gov.uk.ets.registry.api.user.domain.QUser;
import gov.uk.ets.registry.api.user.domain.QUserRoleMapping;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


public class DomainEventEntityRepositoryImpl implements AuditEventDTORepository {

    private final QUser creator = new QUser("creator");
    private final QUserRoleMapping roleMapping = new QUserRoleMapping("roleMapping");
    private final QIamUserRole userRole = new QIamUserRole("userRole");
    private final List<String> excludedAdminRoles = UserRole.getRolesForRoleBasedAccess();
    
    /**
     * The persistence context.
     */
    @PersistenceContext
    EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AuditEventDTO> getAuditEventsByDomainIdOrderByCreationDateDesc(String domainId, boolean isAdmin, List<String> domainTypes) {
        QDomainEventEntity domainEventEntity = QDomainEventEntity.domainEventEntity;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(domainEventEntity.domainId.eq(domainId));
        builder.and(domainEventEntity.domainType.in(domainTypes));
        if (isAdmin) {
            return generateJPAAdminQuery(domainEventEntity, builder);
        } else {
            return generateJPAUserQuery(domainEventEntity, builder);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AuditEventDTO> getSystemEventsByDomainIdOrderByCreationDateDesc(String domainId, List<String> domainTypes) {
        QDomainEventEntity domainEventEntity = QDomainEventEntity.domainEventEntity;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(domainEventEntity.domainId.eq(domainId));
        builder.and(domainEventEntity.creatorType.eq("system"));
        builder.and(domainEventEntity.domainType.in(domainTypes));
        return new JPAQuery<AuditEventDTO>(entityManager).select(new QAuditEventDTO(domainEventEntity.domainId,
            domainEventEntity.domainAction, domainEventEntity.description, Expressions.constant("System"),
            domainEventEntity.creatorType, domainEventEntity.creationDate))
             .from(domainEventEntity).where(builder)
             .orderBy(domainEventEntity.creationDate.desc())
             .fetch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AuditEventDTO> getAuditEventsByCreatorOrderByCreationDateDesc(String urid) {
        QDomainEventEntity domainEventEntity = QDomainEventEntity.domainEventEntity;
        BooleanBuilder builder = new BooleanBuilder();
        builder
            .and(domainEventEntity.domainType.notLike(UploadedFile.class.getName()))
            .and(domainEventEntity.domainType.notLike(Transaction.class.getName()))
            .and(domainEventEntity.domainType.notLike(Account.class.getName()))
            .and(domainEventEntity.domainType.notLike(Task.class.getName()))
            .and(domainEventEntity.domainType.notLike(CompliantEntity.class.getName()))
            .and(domainEventEntity.domainType.notLike(TrustedAccount.class.getName()))
            .and(domainEventEntity.creator.eq(urid)
                .and(domainEventEntity.domainType.notLike(User.class.getName()))              
                )
            .or(domainEventEntity.domainId.eq(urid)
                .and(domainEventEntity.domainType.eq(User.class.getName())
                .and(domainEventEntity.domainAction.notLike("%operator%"))
                .and(domainEventEntity.domainAction.notLike("%Transaction%"))
                .and(domainEventEntity.domainAction.notLike("%Emissions table%"))
                .and(domainEventEntity.domainAction.notLike("%Submit documents for AH task%"))
                .and(domainEventEntity.domainAction.notLike("%ccount%"))
                .and(domainEventEntity.domainAction.notLike("%llocation%"))
                .and(domainEventEntity.domainAction.notLike("%Terms & Conditions%"))
                .and(domainEventEntity.domainAction.notLike("%Notification sent%"))
                .and(domainEventEntity.domainAction.notLike("%User documentation deleted%"))));
        return generateJPAAdminLeftJoinQuery(domainEventEntity, builder);
    }

    private List<AuditEventDTO> generateJPAAdminQuery(QDomainEventEntity domainEventEntity, BooleanBuilder builder) {
        return new JPAQuery<AuditEventDTO>(entityManager).select(new QAuditEventDTO(domainEventEntity.domainId,
            domainEventEntity.domainAction, domainEventEntity.description,
            new CaseBuilder()
            .when(creator.knownAs.isNotEmpty())
            .then(creator.knownAs)
            .otherwise(creator.firstName.append(" ").append(creator.lastName)),
            creator.urid,
            domainEventEntity.creatorType, domainEventEntity.creationDate)).
            from(domainEventEntity).
            innerJoin(creator).
            on(domainEventEntity.creator.eq(creator.urid)).
            where(builder).
            orderBy(domainEventEntity.creationDate.desc()).
            fetch();
    }

    private List<AuditEventDTO> generateJPAAdminLeftJoinQuery(QDomainEventEntity domainEventEntity, BooleanBuilder builder) {
        return new JPAQuery<AuditEventDTO>(entityManager).select(new QAuditEventDTO(domainEventEntity.domainId,
                domainEventEntity.domainAction, domainEventEntity.description,
                new CaseBuilder()
                    .when(creator.knownAs.isNotEmpty())
                    .then(creator.knownAs)
                    .otherwise(creator.firstName.append(" ").append(creator.lastName)),
                creator.urid,
                domainEventEntity.creatorType, domainEventEntity.creationDate)).
            from(domainEventEntity).
            leftJoin(creator).
            on(domainEventEntity.creator.eq(creator.urid)).
            where(builder).
            orderBy(domainEventEntity.creationDate.desc()).
            fetch();
    }

    private List<AuditEventDTO> generateJPAUserQuery(QDomainEventEntity domainEventEntity, BooleanBuilder builder) {
        return new JPAQuery<AuditEventDTO>(entityManager).select(new QAuditEventDTO(domainEventEntity.domainId,
            domainEventEntity.domainAction, domainEventEntity.description, creator.disclosedName, creator.urid,
            domainEventEntity.creatorType, domainEventEntity.creationDate)).
            from(domainEventEntity).
            innerJoin(creator).
            on(domainEventEntity.creator.eq(creator.urid)).
            where(builder.and(creator.notIn(JPAExpressions.select(roleMapping.user)
                .from(roleMapping).innerJoin(userRole).on(roleMapping.role.eq(userRole))
                .where(userRole.roleName.in(excludedAdminRoles))))).
            orderBy(domainEventEntity.creationDate.desc()).
            fetch();
    }
}
