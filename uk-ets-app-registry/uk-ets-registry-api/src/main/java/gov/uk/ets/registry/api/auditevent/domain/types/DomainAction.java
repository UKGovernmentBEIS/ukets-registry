package gov.uk.ets.registry.api.auditevent.domain.types;

public enum DomainAction {
    SUBMIT_DOCS_FOR_AH_TASK_ASSIGNED_COMMENT("Submit documents for AH task assigned. (comment)"),
    SUBMIT_DOCS_FOR_USER_TASK_ASSIGNED_COMMENT("Submit documents for user task assigned. (comment)");

    private final String action;

    DomainAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }
}