package gov.uk.ets.registry.api.file.upload.wrappers;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkArContentValidationWrapper {

    private int accountNumberPosition;
    private int userIdPosition;
    private int permissionPosition;
    private int accountNamePosition;
    private int firstNamePosition;
    private int lastNamePosition;
    private int emailPosition;
    private Set<String> uniqueIdentifiers;
    private Set<String> fileContentExceptions;

    public BulkArContentValidationWrapper() {
        uniqueIdentifiers = new HashSet<>();
        fileContentExceptions = new HashSet<>();
    }
}
