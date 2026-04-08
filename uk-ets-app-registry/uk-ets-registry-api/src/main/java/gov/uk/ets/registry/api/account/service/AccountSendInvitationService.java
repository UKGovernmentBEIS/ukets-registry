package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactSendInvitationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountSendInvitationService {

    private final AccountClaimProcessor processor;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendInvitation(Long identifier, AccountContactSendInvitationDTO dto) {

        processor.processSendInvitation(identifier, dto);
    }
}
