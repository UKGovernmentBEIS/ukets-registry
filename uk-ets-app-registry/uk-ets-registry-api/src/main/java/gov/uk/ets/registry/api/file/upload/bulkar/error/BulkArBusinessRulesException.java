package gov.uk.ets.registry.api.file.upload.bulkar.error;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class BulkArBusinessRulesException extends RuntimeException {

    private final List<String> bulkArErrorList = new ArrayList<>();

    private static final String ERROR_HEADER = "The chosen file contains the following errors:";

    public static BulkArBusinessRulesException create(String bulkArError) {
        BulkArBusinessRulesException bulkArBusinessRulesException =
            new BulkArBusinessRulesException();
        bulkArBusinessRulesException.addError(bulkArError);
        return bulkArBusinessRulesException;
    }

    public void addError(String error) {
        this.bulkArErrorList.add(error);
    }

    public void addErrors(Collection<String> error) {
        this.bulkArErrorList.addAll(error);
        this.bulkArErrorList.sort(Comparator.naturalOrder());
        this.bulkArErrorList.add(0, ERROR_HEADER);
    }
}
