package gov.uk.ets.registry.api.file.upload.requesteddocs.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
public class RequestDocumentsTaskDifference implements Serializable {

    /**
     * TO be replaced for from the uploadedFileInTaskInfo
     */
    @Deprecated
    private List<String> documentNames;

    /**
     * TO be replaced for from the uploadedFileInTaskInfo
     */
    @Deprecated
    private Set<Long> documentIds;
    /**
     * Replaces the previous two so we can keep track of matching document name and id
     */
    private Map<String, Long> uploadedFileNameIdMap;

    private String comment;

    /**
     * Following two needed for
     * {@link gov.uk.ets.registry.api.task.domain.types.RequestType#AH_REQUESTED_DOCUMENT_UPLOAD}
     */
    //TODO: evaluate if the name can be used in all cases so i) keep one way to request account holder documents
    //TODO : ii) avoid fetching a maybe altered account holder name
    private Long accountHolderIdentifier;
    private String accountHolderName;

    /**
     * Following  needed for
     * {@link gov.uk.ets.registry.api.task.domain.types.RequestType#AR_REQUESTED_DOCUMENT_UPLOAD}
     */
    private String userUrid;
    
    private String accountName;

}
