package gov.uk.ets.registry.api.ar.infrastructure;

import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.account.domain.QAccount;
import gov.uk.ets.registry.api.ar.domain.ARUpdateAction;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionRepository;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.task.domain.QTask;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link ARUpdateActionRepository} implementation
 */
@AllArgsConstructor
@Repository
public class ARUpdateActionRepositoryImpl implements ARUpdateActionRepository {
    private static final QTask task = QTask.task;
    private static final QAccount account = QAccount.account;

    private EntityManager entityManager;
    private final Mapper mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ARUpdateAction> fetchByAccountId(long accountIdentifier) {
        return new JPAQuery<Task>(entityManager).from(task)
            .join(task.account, account)
            .on(account.identifier.eq(accountIdentifier)
                .and(task.type.in(RequestType.getARUpdateTasks()))
                .and(task.status.eq(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED)))
            .fetch().stream().map(t ->
                mapper.convertToPojo(t.getDifference(), ARUpdateAction.class))
            .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ARUpdateAction> fetchPendingArAdditionsByAccountId(long accountIdentifier) {
        return new JPAQuery<Task>(entityManager).from(task)
            .join(task.account, account)
            .on(account.identifier.eq(accountIdentifier)
                .and(task.type.in(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST))
                .and(task.status.eq(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED)))
            .fetch().stream().map(t ->
                mapper.convertToPojo(t.getDifference(), ARUpdateAction.class))
            .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ARUpdateAction> fetchPendingArUpdateActionsByType(RequestType type) {
    	if(type == null || !RequestType.getARUpdateTasks().contains(type)) {
    		return Collections.emptyList();
    	}
        return new JPAQuery<Task>(entityManager).from(task)
            .where(task.type.eq(type)
            		.and(task.status.eq(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED)))
            .fetch().stream().map(t ->
                mapper.convertToPojo(t.getDifference(), ARUpdateAction.class))
            .collect(Collectors.toList());
    }
}
