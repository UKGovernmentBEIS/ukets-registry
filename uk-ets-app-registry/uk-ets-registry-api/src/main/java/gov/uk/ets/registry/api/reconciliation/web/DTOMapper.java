package gov.uk.ets.registry.api.reconciliation.web;

import gov.uk.ets.registry.api.reconciliation.domain.Reconciliation;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class DTOMapper {
    public ReconciliationDTO map(Reconciliation reconciliation) {
        if(reconciliation == null) {
            return null;
        }
        return ReconciliationDTO.builder()
            .created(formatDate(reconciliation.getCreated()))
            .updated(formatDate(reconciliation.getUpdated()))
            .identifier(reconciliation.getIdentifier())
            .status(reconciliation.getStatus())
            .build();
    }

    private String formatDate(Date date) {
        if(date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        return sdf.format(date);
    }
}
