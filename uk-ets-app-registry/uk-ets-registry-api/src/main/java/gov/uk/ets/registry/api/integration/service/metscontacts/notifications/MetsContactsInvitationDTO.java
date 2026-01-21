package gov.uk.ets.registry.api.integration.service.metscontacts.notifications;

import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactSendInvitationDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetsContactsInvitationDTO {

    private AccountDTO accountDTO;
    private AccountContactSendInvitationDTO sendInvitationDTO;
    private String accountClaimCode;
}
