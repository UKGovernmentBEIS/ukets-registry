package gov.uk.ets.registry.api.common.model.services;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepresentativeRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.repository.AircraftOperatorRepository;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.account.repository.InstallationRepository;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.common.model.repositories.ContactRepository;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the persistence service.
 */
@Service
public class PersistenceServiceImpl implements PersistenceService {

    @Autowired
    private AccountAccessRepository accountAccessRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AircraftOperatorRepository aircraftOperatorRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private CompliantEntityRepository compliantEntityRepository;

    @Autowired
    private InstallationRepository installationRepository;

    @Autowired
    private AccountHolderRepresentativeRepository legalRepresentativeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public <E> E save(E entity) {
        E result = null;

        if (entity instanceof AccountAccess) {
            result = (E)accountAccessRepository.save((AccountAccess)entity);

        } else if (entity instanceof AccountHolder) {
            result = (E)accountHolderRepository.save((AccountHolder)entity);

        } else if (entity instanceof Account) {
                    result = (E)accountRepository.save((Account)entity);

        } else if (entity instanceof CompliantEntity) {
            result = (E)compliantEntityRepository.save((CompliantEntity) entity);

        } else if (entity instanceof AircraftOperator) {
            result = (E)aircraftOperatorRepository.save((AircraftOperator)entity);

        } else if (entity instanceof Contact) {
                    result = (E)contactRepository.save((Contact)entity);

        } else if (entity instanceof Installation) {
                    result = (E)installationRepository.save((Installation)entity);

        } else if (entity instanceof AccountHolderRepresentative) {
                    result = (E)legalRepresentativeRepository.save((AccountHolderRepresentative)entity);

        } else if (entity instanceof User) {
                    result = (E)userRepository.save((User)entity);

         } else if (entity instanceof Task) {
                    result = (E)taskRepository.save((Task)entity);

        } else {
            throw new IllegalArgumentException(String.format("Entity not yet supported: %s", entity.getClass()));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long getNextBusinessIdentifier(Class clazz) {
        Long result = null;

        if (Account.class.equals(clazz)) {
            result = accountRepository.getNextIdentifier();

        } else if (AccountHolder.class.equals(clazz)) {
            result = accountHolderRepository.getNextIdentifier();

       } else if (Task.class.equals(clazz)) {
            result = taskRepository.getNextRequestId();

        } else {
            throw new IllegalArgumentException(String.format("Type not supported: %s", clazz));
        }

        return result;
    }
}
