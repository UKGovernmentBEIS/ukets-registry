package gov.uk.ets.registry.api.integration.changelog.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountDTOFactory;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.integration.changelog.DomainObject;
import gov.uk.ets.registry.api.integration.changelog.domain.IntegrationChangeLog;
import gov.uk.ets.registry.api.integration.changelog.repository.IntegrationChangeLogRepository;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.object.ValueObjectId;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class CommonAuditService {

    private final AccountRepository accountRepository;
    private final AccountDTOFactory accountDTOFactory;
    private final IntegrationChangeLogRepository changeLogRepository;

    public AccountDTO getAccountDto(Long identifier) {
        return toAccountDto(getAccount(identifier));
    }

    public AccountDTO toAccountDto(Account account) {
        return accountDTOFactory.create(account, true);
    }

    public Account getAccount(Long identifier) {
        return accountRepository.findByIdentifierWithAccountHolder(identifier)
                .orElseThrow(() -> new RuntimeException("Account does not exist"));
    }

    public Account getAccountByCompliantEntity(Long compliantEntityIdentifier) {
        return accountRepository.findByCompliantEntityIdentifier(compliantEntityIdentifier)
                .orElseThrow(() -> new RuntimeException("Account does not exist"));
    }

    public boolean handleChanges(DomainObject domainObj, Object oldValue, Object newValue,
                                 Long entityId, String fullIdentifier, Long operatorId,
                                 SourceSystem updatedBy) {


        Javers javers = JaversBuilder.javers().build();

        Diff diff = javers.compare(oldValue, newValue);
        if(diff.hasChanges()) {
            Changes changes = diff.getChanges();

            Date now = new Date();
            changes.stream()
                    .map(change -> (PropertyChange<?>) change)
                    .filter(this::ignoreNullEmptyChanges)
                    .forEach(change -> {
                        IntegrationChangeLog changeLog = new IntegrationChangeLog();

                        String objName = getObjectName(change);
                        String propertyName = objName.isEmpty() ? change.getPropertyName() : objName + "." + change.getPropertyName();

                        changeLog.setFieldChanged(domainObj.getDescription() + "." + propertyName);
                        changeLog.setOldValue(change.getLeft() != null ? change.getLeft().toString() : null);
                        changeLog.setNewValue(change.getRight() != null ? change.getRight().toString() : null);
                        changeLog.setEntity(domainObj.getEntity());
                        changeLog.setEntityId(entityId);
                        changeLog.setAccountNumber(fullIdentifier);
                        changeLog.setOperatorId(operatorId);
                        changeLog.setUpdatedAt(now);
                        changeLog.setUpdatedBy(updatedBy.getLabel());

                        changeLogRepository.save(changeLog);
                    });
        }
        return diff.hasChanges();
    }

    private String getObjectName(PropertyChange<?> change) {
        if (change.getAffectedGlobalId() instanceof ValueObjectId valueObjectId) {
            return valueObjectId.getFragment();
        }
        return "";
    }

    private boolean ignoreNullEmptyChanges(PropertyChange<?> change) {
        String left = Optional.ofNullable(change.getLeft()).map(Object::toString).orElse("");
        String right = Optional.ofNullable(change.getRight()).map(Object::toString).orElse("");
        return !left.equals(right);
    }

    public void handleChanges(DomainObject domainObj, String propertyName,
                              Object oldValue, Object newValue, Long entityId,
                              String fullIdentifier, Long operatorId, SourceSystem updatedBy) {

        IntegrationChangeLog changeLog = new IntegrationChangeLog();
        changeLog.setFieldChanged(propertyName);
        changeLog.setOldValue(oldValue.toString());
        changeLog.setNewValue(newValue.toString());
        changeLog.setEntity(domainObj.getEntity());
        changeLog.setEntityId(entityId);
        changeLog.setAccountNumber(fullIdentifier);
        changeLog.setOperatorId(operatorId);
        changeLog.setUpdatedAt(new Date());
        changeLog.setUpdatedBy(updatedBy.getLabel());

        changeLogRepository.save(changeLog);
    }
}
