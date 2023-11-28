package gov.uk.ets.registry.api.task.shared;

public class TaskPropertyPath {
    /**
     * The request id entity property path
     */
    public static final String TASK_REQUEST_ID = "task.requestId";
    /**
     * The task type entity property path
     */
    public static final String TASK_TYPE = "task.type";
    /**
     * The first name of initiator of task entity property path
     */
    public static final String TASK_INITIATOR_FIRST_NAME = "task.initiatedBy.firstName";
    /**
     * The last name of initiator of task entity property path
     */
    public static final String TASK_INITIATOR_LAST_NAME = "task.initiatedBy.lastName";
    /**
     * The first name of claimant of task entity property path
     */
    public static final String TASK_CLAIMANT_FIRST_NAME = "task.claimedBy.firstName";
    /**
     * The last name of claimant of task entity property path
     */
    public static final String TASK_CLAIMANT_LAST_NAME = "task.claimedBy.lastName";
    /**
     * The account identifier entity property path
     */
    public static final String ACCOUNT_IDENTIFIER = "account.identifier";
    /**
     * The account kyoto type entity property path
     */
    public static final String ACCOUNT_KYOTO_TYPE = "account.kyotoAccountType";
    /**
     * The account registry type property path
     */
    public static final String ACCOUNT_REGISTRY_TYPE = "account.registryAccountType";

    public static final String ACCOUNT_TYPE_LABEL = "account.accountType";
    /**
     * The account holder name property path
     */
    public static final String ACCOUNT_HOLDER_NAME = "account.holder.name";
    /**
     * The task created date entity property path
     */
    public static final String TASK_CREATED_DATE = "task.initiatedDate";
    /**
     * The task status entity property path
     */
    public static final String TASK_STATUS = "task.status";
    /**
     * The task search metadata value for account type property path
     */
    public static final String TASK_SEARCH_METADATA_TYPE_VALUE = "taskSearchMetadataForType.metadataValue";
    /**
     * The task search metadata value for account holder property path
     */
    public static final String TASK_SEARCH_METADATA_AH_VALUE = "taskSearchMetadataForAh.metadataValue";
    /**
     * The task search metadata value for allocation category property path
     */
    public static final String TASK_SEARCH_METADATA_ALLOCATION_CATEGORY_VALUE = "taskSearchMetadataForAllocationCategory.metadataValue";
    /**
     * The task search metadata value for allocation year property path
     */
    public static final String TASK_SEARCH_METADATA_ALLOCATION_YEAR_VALUE = "taskSearchMetadataForAllocationYear.metadataValue";

    private TaskPropertyPath() {}
}
