package gov.uk.ets.registry.api.integration.changelog.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.MetsAccountContact;
import gov.uk.ets.registry.api.account.repository.AccountContactRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountDTOFactory;
import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsContactDTO;
import gov.uk.ets.registry.api.integration.changelog.DomainObject;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class ContactsAuditService {

    private final AccountRepository accountRepository;
    @Value("${account.audit.enabled:true}")
    private boolean auditEnabled;

    private final CommonAuditService commonAuditService;
    private final AccountContactRepository accountContactRepository;
    private final AccountDTOFactory accountDTOFactory;

    public List<MetsContactDTO> getMetsContactDTO(Long complianceEntityIdentifier) {
        Account account = accountRepository.findByCompliantEntityIdentifier(complianceEntityIdentifier)
                .orElseThrow(() -> new RuntimeException("Account does not exist"));

        return toMetContactsListDto(account.getIdentifier());
    }

    public void logChanges(List<MetsContactDTO> current, List<MetsAccountContact> updated, SourceSystem updatedBy,
                           String fullIdentifier, Long complianceEntityIdentifier
                           ) {

        if (!auditEnabled) {
            return;
        }

        handleMetsContactsChanges(
                DomainObject.UPDATE_ACCOUNT_CONTACT_LIST,
                fullIdentifier,
                complianceEntityIdentifier,
                updatedBy,
                current,
                updated
        );
    }

    private void handleMetsContactsChanges(
            DomainObject domainObj,
            String fullIdentifier,
            Long operatorId,
            SourceSystem updatedBy,
            List<MetsContactDTO> existingMetsContacts,
            List<MetsAccountContact> updatedMetsContacts
    ) {

        updatedMetsContacts.forEach(newVersion -> {
            MetsContactDTO current = null;
            Optional<MetsContactDTO> existing = existingMetsContacts.stream()
                    .filter(existingMetsContact ->
                            existingMetsContact.getEmail().equals(newVersion.getEmailAddress())).findFirst();

            if(existing.isPresent()) {
                current = existing.get();
            }

            MetsContactDTO newVersionDTO = toMetContactDto(newVersion);

            commonAuditService.handleChanges(domainObj,
                    current,
                    newVersionDTO,
                    newVersion.getId(),
                    fullIdentifier,
                    operatorId,
                    updatedBy);
        });
    }

    private List<MetsContactDTO> toMetContactsListDto(Long accountIdentifier) {
        return accountDTOFactory.createMetsContacts(accountIdentifier);
    }

    private MetsContactDTO toMetContactDto(MetsAccountContact contact) {
        return accountDTOFactory.createMetsContactDTO(contact);
    }
}
