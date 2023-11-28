package gov.uk.ets.registry.api.notification;

import java.util.Set;

import gov.uk.ets.registry.api.file.upload.requesteddocs.model.DocumentsRequestType;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DocumentRequestGroupNotification extends EmailNotification {

    private String requestId;
    private DocumentsRequestType documentsRequestType;
    private String userId;
    private String userFullName;
    private String accountName;
    private String accountHolderName;

    @Builder
    public DocumentRequestGroupNotification(Set<String> recipients, GroupNotificationType type, String requestId, DocumentsRequestType documentsRequestType,
                                            String userId, String userFullName, String accountName, String accountHolderName) {
        super(recipients, type, null, null, null);
        this.requestId = requestId;
        this.documentsRequestType = documentsRequestType;
        this.userId = userId;
        this.accountName = accountName;
        this.accountHolderName = accountHolderName;
        this.userFullName = userFullName;
    }
}
