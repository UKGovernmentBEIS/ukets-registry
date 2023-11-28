package gov.uk.ets.registry.api.file.upload.allocationtable.notification;

import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.notification.EmailNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@Setter
public class UploadAllocationTableEmailNotification extends EmailNotification {


    private static final long serialVersionUID = 1L;
    private String requestId;
    private AllocationType allocationType;
    
    public UploadAllocationTableEmailNotification(Set<String> recipients, GroupNotificationType type, AllocationType allocationType,String requestId){
        super(recipients, type, null, null, null);
        this.allocationType = allocationType;
        this.requestId = requestId;
    }
}
