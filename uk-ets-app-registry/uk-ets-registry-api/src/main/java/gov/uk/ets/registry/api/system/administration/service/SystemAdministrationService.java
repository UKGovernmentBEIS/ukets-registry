package gov.uk.ets.registry.api.system.administration.service;

import gov.uk.ets.registry.api.system.administration.web.model.ResetDatabaseActionResult;

/**
 * Defines available operations for system administration.
 * @author fragkise
 * @since v0.9.0
 */
public interface SystemAdministrationService {

    /**
     * Resets the db in an initial specified state.Includes the following:
     * <ol>
     * <li>Delete all accounts , transactions , tasks and unit blocks.</li>
     * <li>Deletes all users from both the db and Keycloak.</li>
     * <li>Inserts a standard set of users to both db and Keycloak specified in uk-ets-realm-sample-users.json</li>
     * <li>Inserts a government accounts as defined in a uk-ets-government-accounts.json file</li>
     * <li>Inserts a set of Accounts specified in a uk-ets-sample-accounts.json file.</li>
     * </ol>
     *
     * @return result object containing statistics about the operation.
     */
    ResetDatabaseActionResult reset();
}
