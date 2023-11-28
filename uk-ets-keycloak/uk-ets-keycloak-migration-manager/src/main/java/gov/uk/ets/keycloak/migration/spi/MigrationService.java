package gov.uk.ets.keycloak.migration.spi;

import gov.uk.ets.keycloak.migration.jpa.Migration;
import org.keycloak.provider.Provider;

public interface MigrationService extends Provider {

    Migration findByKey(String key);

    Migration addMigration(String key);

}
