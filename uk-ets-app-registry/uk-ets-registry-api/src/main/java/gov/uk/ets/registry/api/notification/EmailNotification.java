package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import gov.uk.ets.registry.usernotifications.MultipartEmailWithSubject;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmailNotification implements GroupNotification {
    private Set<String> recipients = new HashSet<>();
    private GroupNotificationType type;
    private String subject;
    private String bodyHtml;
    private String bodyPlain;
    /**
     * Notifies mail service if it should include Bcc recipients or not.
     */
    private boolean includeBcc;

    public EmailNotification(Set<String> recipients, GroupNotificationType type, String subject, String bodyHtml,
                             String bodyPlain) {
        this.recipients = recipients;
        this.type = type;
        this.subject = subject;
        this.bodyHtml = bodyHtml;
        this.bodyPlain = bodyPlain;
    }

    @Override
    public String subject() {
        return subject;
    }

    @Override
    public String bodyHtml() {
        return bodyHtml;
    }

    @Override
    public String bodyPlain() {
        return bodyPlain;
    }

    @Override
    public Set<String> recipients() {
        return recipients;
    }

    @Override
    public GroupNotificationType type() {
        return type;
    }

    public EmailNotification enrichWithEmailContext(MultipartEmailWithSubject email) {
        this.setSubject(email.subject());
        this.setBodyHtml(email.bodyHtml());
        this.setBodyPlain(email.bodyPlain());
        return this;
    }
}
