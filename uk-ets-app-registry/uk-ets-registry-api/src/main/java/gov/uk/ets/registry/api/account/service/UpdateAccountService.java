package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.*;
import gov.uk.ets.registry.api.account.repository.*;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class UpdateAccountService {

    private final static Map<OperatorType, RequestType> OPERATOR_TO_REQUEST_TYPE = Map.of(
            OperatorType.INSTALLATION, RequestType.INSTALLATION_OPERATOR_UPDATE_REQUEST,
            OperatorType.AIRCRAFT_OPERATOR, RequestType.AIRCRAFT_OPERATOR_UPDATE_REQUEST,
            OperatorType.MARITIME_OPERATOR, RequestType.MARITIME_OPERATOR_UPDATE_REQUEST);

    private final AccountRepository accountRepository;
    private final AccountOperatorUpdateService accountOperatorUpdateService;
    private final AccountService accountService;

    public Account updateAccount(AccountDTO accountDTO) {
        Account account = accountRepository.findByCompliantEntityIdentifier(accountDTO.getIdentifier())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        setIfNeededEmitterIDasMonitoringPlanID(accountDTO, account);

        AccountHolderDTO accountHolderDTO = accountDTO.getAccountHolder();
        accountHolderDTO.setId(account.getAccountHolder().getIdentifier());
        accountHolderDTO.setType(account.getAccountHolder().getType());
        accountService.updateHolder(accountHolderDTO);

        OperatorDTO operatorDTO = accountDTO.getOperator();
        accountOperatorUpdateService.updateOperator(operatorDTO,
                accountDTO.getIdentifier(),
                OPERATOR_TO_REQUEST_TYPE.get(OperatorType.valueOf(operatorDTO.getType())),
                account);
        return accountRepository.save(account);
    }

    private void setIfNeededEmitterIDasMonitoringPlanID(AccountDTO accountDTO, Account account) {
        if ((
                RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.name().equals(accountDTO.getOperator().getType())
                || RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT.name().equals(accountDTO.getOperator().getType()))
                && accountDTO.getOperator().getMonitoringPlan() == null) {

            Object ce = Hibernate.unproxy(account.getCompliantEntity());
            if (ce instanceof AircraftOperator ao) {
                ao.setMonitoringPlanIdentifier(account.getCompliantEntity().getEmitterId());
            } else if (ce instanceof MaritimeOperator mo) {
                mo.setMaritimeMonitoringPlanIdentifier(account.getCompliantEntity().getEmitterId());
            }
        }
    }

}

