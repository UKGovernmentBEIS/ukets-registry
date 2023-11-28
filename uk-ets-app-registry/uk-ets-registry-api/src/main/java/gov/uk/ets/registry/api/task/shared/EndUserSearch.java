package gov.uk.ets.registry.api.task.shared;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EndUserSearch {
    /**
     * Whether the search originates from admin.
     */
    private Boolean adminSearch;
    
    private String iamIdentifier;        
    
}
