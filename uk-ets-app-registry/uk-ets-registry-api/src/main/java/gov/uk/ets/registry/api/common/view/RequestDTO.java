package gov.uk.ets.registry.api.common.view;

import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Request transfer object.
 */
@Getter
@Setter
public class RequestDTO implements Serializable {

    /**
     * Constructor.
     */
    public RequestDTO() {
        // nothing to implement here.
    }

    /**
     * Constructor.
     * @param requestId The request id.
     * @param type The request type.
     * @param status The request status.
     */
    public RequestDTO(Long requestId, RequestType type, RequestStateEnum status) {
        this.requestId = requestId;
        this.type = type;
        this.status = status;
    }

    /**
     * The request id.
     */
    private Long requestId;

    /**
     * The request type.
     */
    private RequestType type;

    /**
     * The request status.
     */
    private RequestStateEnum status;

}
