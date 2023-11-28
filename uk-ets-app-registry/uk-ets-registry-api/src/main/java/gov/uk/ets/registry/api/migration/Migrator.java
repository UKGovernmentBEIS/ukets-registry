package gov.uk.ets.registry.api.migration;

/**
 * Interface that defines a contract among migrator services, in order to execute actions
 * during the registry application startup.
 */
public interface Migrator {

    /**
     * Method that wraps the migration execution.
     */
    void migrate();
}
