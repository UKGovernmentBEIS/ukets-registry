package gov.uk.ets.registry.api.file.upload.allocationtable.error;

import lombok.Getter;
import lombok.Setter;

/**
 * Indicates an invalid year in filename during allocation upload.
 * @author P35036
 *
 */
@Getter
@Setter
@SuppressWarnings("serial")
public class AllocationTableYearException extends RuntimeException {

    private String year;
    
    public AllocationTableYearException(String year) {
        this.year = year;
    }    
    
    
    @Override
    public String getMessage() {        
        return AllocationTableError.INVALID_COLUMNS_YEAR.getMessage() + " Year:" + year;
    }
}
