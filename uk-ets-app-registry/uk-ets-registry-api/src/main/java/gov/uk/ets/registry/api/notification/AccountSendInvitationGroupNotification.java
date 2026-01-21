package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountSendInvitationGroupNotification extends EmailNotification {

    private String contactFullName;
    private String operatorName;
    private String accountType;
    private String accountNumber;
    private String emitterId;
    private String accountClaimCode;
    private Boolean isMetsContact;

    @Builder
    public AccountSendInvitationGroupNotification(Set<String> recipients, GroupNotificationType type,
                                                  String contactFullName, String operatorName,
                                                  String accountType, String accountNumber,
                                                  String emitterId, String accountClaimCode, Boolean isMetsContact) {
        super(recipients, type, null, null, null);
        this.contactFullName = contactFullName;
        this.operatorName = operatorName;
        this.accountType = accountType;
        this.accountNumber = accountNumber;
        this.emitterId = emitterId;
        this.accountClaimCode = accountClaimCode;
        this.isMetsContact = isMetsContact;
    }
}
