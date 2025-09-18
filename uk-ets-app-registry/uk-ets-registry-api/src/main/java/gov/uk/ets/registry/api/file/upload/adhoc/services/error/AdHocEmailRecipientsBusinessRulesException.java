package gov.uk.ets.registry.api.file.upload.adhoc.services.error;

import java.util.Map;
import java.util.Set;
import lombok.Getter;

@Getter
public class AdHocEmailRecipientsBusinessRulesException extends RuntimeException {

    private final Map<AdHocEmailRecipientsError, Set<Integer>> adHocEmailRecipientsErrors;

    public AdHocEmailRecipientsBusinessRulesException(Map<AdHocEmailRecipientsError, Set<Integer>> errors) {
        this.adHocEmailRecipientsErrors = errors;
    }
}
