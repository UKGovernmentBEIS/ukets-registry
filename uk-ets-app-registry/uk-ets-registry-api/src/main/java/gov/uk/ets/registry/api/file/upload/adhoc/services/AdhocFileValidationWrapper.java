package gov.uk.ets.registry.api.file.upload.adhoc.services;

import gov.uk.ets.registry.api.file.upload.adhoc.services.error.AdHocEmailRecipientsError;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdhocFileValidationWrapper {

    private Map<AdHocEmailRecipientsError, Set<Integer>> errors;
    private Integer tentativeRecipients;
}
