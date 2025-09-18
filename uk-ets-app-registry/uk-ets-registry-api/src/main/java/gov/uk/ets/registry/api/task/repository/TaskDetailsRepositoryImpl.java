package gov.uk.ets.registry.api.task.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.account.domain.QAccount;
import gov.uk.ets.registry.api.task.domain.QTask;
import gov.uk.ets.registry.api.task.domain.QTaskTransaction;
import gov.uk.ets.registry.api.task.web.model.QTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.user.domain.QUser;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class TaskDetailsRepositoryImpl implements TaskDetailsRepository {
    private static final QTask task = QTask.task;
    private static final QTaskTransaction taskTransaction = QTaskTransaction.taskTransaction;
    private static final QAccount account = QAccount.account;
    private static final QUser user = QUser.user;
    private final QUser initiatedBy = new QUser("initiatedBy");
    private final QUser claimedBy = new QUser("claimedBy");
    private final QTask parentTask = new QTask("parentTask");
    /**
     * The persistence context.
     */
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public TaskDetailsDTO getTaskDetails(Long requestId) {
        
        JPAQuery<TaskDetailsDTO> query = taskDetailsQuery()
            .where(task.requestId.eq(requestId));
        return groupBy(query).fetchOne();

    }

    @Override
    public List<TaskDetailsDTO> getSubTaskDetails(Long parentRequestId) {
        JPAQuery<TaskDetailsDTO> query = taskDetailsQuery()
            .where(task.parentTask.requestId.eq(parentRequestId));
        return groupBy(query).orderBy(task.deadline.desc().nullsLast()).fetch();
    }

    private JPAQuery<TaskDetailsDTO> taskDetailsQuery() {
        return new JPAQuery<>(entityManager)
            .select(
                new QTaskDetailsDTO(task.requestId, task.type, task.status, initiatedBy.firstName, initiatedBy.lastName,
                    initiatedBy.id, initiatedBy.urid, initiatedBy.knownAs,
                    task.initiatedDate, claimedBy.firstName, claimedBy.lastName, claimedBy.knownAs, claimedBy.urid, task.claimedDate,
                    account.identifier, account.fullIdentifier, account.accountName, user.firstName, user.lastName,
                    user.urid, task.file,
                    Expressions.stringTemplate("string_agg({0},{1})", taskTransaction.transactionIdentifier,","),                  
                    task.difference, task.before, parentTask.requestId, parentTask.type, task.completedDate, task.deadline))
            .from(task)
            .leftJoin(task.account, account)
            .leftJoin(task.initiatedBy, initiatedBy)
            .leftJoin(task.claimedBy, claimedBy)
            .leftJoin(task.parentTask, parentTask)
            .leftJoin(task.user, user)
            .leftJoin(task.transactionIdentifiers,taskTransaction);            
    }
    
    private JPAQuery<TaskDetailsDTO> groupBy(JPAQuery<TaskDetailsDTO> query) {
        return query.groupBy(task.requestId, task.type, task.status, initiatedBy.firstName, initiatedBy.lastName,
            initiatedBy.id, initiatedBy.urid, initiatedBy.knownAs,
            task.initiatedDate, claimedBy.firstName, claimedBy.lastName, claimedBy.knownAs, claimedBy.urid, task.claimedDate,
            account.identifier, account.fullIdentifier, account.accountName, user.firstName, user.lastName,
            user.urid, task.file,
            task.difference, task.before, parentTask.requestId, parentTask.type, task.completedDate, task.deadline);
    }
}
