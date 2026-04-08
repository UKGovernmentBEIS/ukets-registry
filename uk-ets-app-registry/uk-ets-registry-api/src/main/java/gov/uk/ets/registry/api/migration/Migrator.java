package gov.uk.ets.registry.api.migration;

/**
 * Interface that defines a contract among migrator services, in order to execute actions
 * during the registry application startup.
 * 
 * @deprecated as of Shedlock v7.6.0 and registry-api v5.25.0 
 *     use net.javacrumbs.shedlock.core.LockingTaskExecutor.Task instead.
 */
@Deprecated
public interface Migrator {

    /**
     * Method that wraps the migration execution.
     */
    @Deprecated
    void migrate();
}
