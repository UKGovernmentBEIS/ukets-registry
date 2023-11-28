package gov.uk.ets.registry.api.file.upload.wrappers;

import gov.uk.ets.registry.api.file.upload.allocationtable.error.AllocationTableUploadBusinessError;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllocationTableContentValidationWrapper {

    private String checksum;
    private String[] fileNameArray;
    private int minimumTableYearColumn;
    private int maximumTableYearColumn;
    private List<String> years;

    private Set<String> compliantEntityIdentifiers;
    private Set<String> allocationEntries;
    private Set<String> allocationTableCompliantEntityIdentifiers;
    private Set<String> uniqueIdentifiers;
    private List<AllocationTableUploadBusinessError> fileContentExceptions;

    public AllocationTableContentValidationWrapper() {
        compliantEntityIdentifiers = new HashSet<>();
        allocationEntries = new HashSet<>();
        allocationTableCompliantEntityIdentifiers = new HashSet<>();
        uniqueIdentifiers = new HashSet<>();
        fileContentExceptions = new ArrayList<>();
    }
}
