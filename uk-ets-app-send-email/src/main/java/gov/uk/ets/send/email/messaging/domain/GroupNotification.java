package gov.uk.ets.send.email.messaging.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GroupNotification implements Serializable {

    private static final long serialVersionUID = -5697043565664402556L;

    private Set<String> recipients = new HashSet<>();
    private String subject;
    private String bodyHtml;
    private String bodyPlain;
    private boolean includeBcc;
}
