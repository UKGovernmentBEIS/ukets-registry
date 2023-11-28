package gov.uk.ets.registry.api.task.web.model;

public class TaskBulkActionSuccess {
    private Integer updated;

    public TaskBulkActionSuccess(Integer updated) {
        this.updated = updated;
    }

    public Integer getUpdated() {
        return updated;
    }
}
