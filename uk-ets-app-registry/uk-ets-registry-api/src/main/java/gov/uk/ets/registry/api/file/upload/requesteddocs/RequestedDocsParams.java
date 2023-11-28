package gov.uk.ets.registry.api.file.upload.requesteddocs;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.user.domain.User;
import java.util.Set;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.web.multipart.MultipartFile;

import static gov.uk.ets.commons.logging.RequestParamType.FILE_ID;

/**
 * A parameters pojo for sending when uploading requested documents.
 */
@Data
public class RequestedDocsParams {

    @MDCParam(FILE_ID)
    private Long fileId;
    /**
     * The binary file.
     */
    private MultipartFile file;

    /**
     * Document name as requested by the administrator.
     */
    private String documentName;

    /**
     * Account holder identifier {@link AccountHolder#getIdentifier()}.
     */
    private Long accountHolderIdentifier;

    /**
     * User identifier {@link User#getUrid()}.
     */
    private String userUrid;

    /**
     * Task Request identifier {@link Task#getRequestId()}.
     */
    private Long taskRequestId;

    /**
     * This is a helper attribute to hold the ids of the files
     * stored in the database for the specific task. All these ids are stored in the diff of the task.
     */
    @Transient
    private Set<Long> totalFileUploads;
    
}
