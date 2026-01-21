package gov.uk.ets.registry.api.helper.integration;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.MaritimeOperator;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountLoader {

    @Autowired
    private AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public Account loadAccountWithRelations(Long id) {
        Account account = accountRepository.findByIdentifier(id).orElseThrow();

        // initialize lazy relationships
        Hibernate.initialize(account.getAccountHolder());
        Hibernate.initialize(account.getCompliantEntity());

        // safely unproxy the CompliantEntity
        CompliantEntity ce = account.getCompliantEntity();
        if (ce != null) {
            if (RegistryAccountType.OPERATOR_HOLDING_ACCOUNT.equals(account.getRegistryAccountType())) {
                Installation installation = (Installation) Hibernate.unproxy(account.getCompliantEntity());
                Hibernate.initialize(installation.getActivityTypes());
                account.setCompliantEntity(installation);  // replace proxy with real entity
            } else if (RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT
                .equals(account.getRegistryAccountType())) {
                AircraftOperator aircraftOperator =
                    (AircraftOperator) Hibernate.unproxy(account.getCompliantEntity());
                account.setCompliantEntity(aircraftOperator);  // replace proxy with real entity
            }
            else if (RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT
                .equals(account.getRegistryAccountType())) {
                MaritimeOperator maritimeOperator =
                    (MaritimeOperator) Hibernate.unproxy(account.getCompliantEntity());
                account.setCompliantEntity(maritimeOperator);  // replace proxy with real entity
            }
        }


        Hibernate.initialize(account.getMetsAccountContacts());
        return account;
    }

    @Transactional(readOnly = true)
    public Account loadDomainEntity(Long id) {
        Account account = accountRepository.findByIdentifier(id).orElseThrow();

        // initialize lazy relationships
        Hibernate.initialize(account.getAccountHolder());
        Hibernate.initialize(account.getCompliantEntity());

        // safely unproxy the CompliantEntity
        CompliantEntity ce = account.getCompliantEntity();
        if (ce != null) {
            if (RegistryAccountType.OPERATOR_HOLDING_ACCOUNT.equals(account.getRegistryAccountType())) {
                Installation installation = (Installation) Hibernate.unproxy(account.getCompliantEntity());
                Hibernate.initialize(installation.getActivityTypes());
                account.setCompliantEntity(installation);  // replace proxy with real entity
            } else if (RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT
                .equals(account.getRegistryAccountType())) {
                AircraftOperator aircraftOperator =
                    (AircraftOperator) Hibernate.unproxy(account.getCompliantEntity());
                account.setCompliantEntity(aircraftOperator);  // replace proxy with real entity
            }
            else if (RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT
                .equals(account.getRegistryAccountType())) {
                MaritimeOperator maritimeOperator =
                    (MaritimeOperator) Hibernate.unproxy(account.getCompliantEntity());
                account.setCompliantEntity(maritimeOperator);  // replace proxy with real entity
            }
        }

        return account;
    }
}

