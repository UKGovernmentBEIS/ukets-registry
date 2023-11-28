package gov.uk.ets.registry.api.file.upload.emissionstable.error;

import java.io.Serializable;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

/**
 * The Emissions Upload action error.
 */
@Getter
@Builder
public class EmissionsUploadActionError implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Error code for the case that the emissions file uploaded contains business errors.
     */
    public static final String EMISSIONS_FILE_BUSINESS_ERRORS = "EMISSIONS_FILE_BUSINESS_ERRORS";
    
	/**
     * Error code for the case that there is an existing upload request not yet approved.
     */
    public static final String PENDING_EMISSIONS_FILE_UPLOAD = "PENDING_EMISSIONS_FILE_UPLOAD";

    /**
     * Error code for the case of an invalid OTP code.
     */
    public static final String INVALID_OTP_CODE = "INVALID_OTP_CODE";

    
    /**
     * The error code.
     */
    String code;
    /**
     * The error message.
     */
    String message;
    /**
     * The component Identifier.
     */
    String componentId;
    /**
     * The identifier of the error file.
     */
    Long errorFileId;
    /**
     * The name of the error file.
     */
    String errorFilename;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmissionsUploadActionError that = (EmissionsUploadActionError) o;
        return code.equals(that.code) &&
            Objects.equals(componentId, that.componentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, componentId);
    }
}
