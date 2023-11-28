package gov.uk.ets.keycloak.migration.spi.impl;

import gov.uk.ets.keycloak.migration.jpa.Migration;
import gov.uk.ets.keycloak.migration.spi.MigrationService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.EntityManager;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;

public class MigrationServiceImpl implements MigrationService {

    private final KeycloakSession session;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public MigrationServiceImpl(KeycloakSession session) {
        this.session = session;
    }

    /**
     * Retrieves migration by primary key.
     */
    public Migration findByKey(String key) {
        return getEntityManager().find(Migration.class, key);
    }

    /**
     * Saves migration to the database using the provided key and a current timestamp.
     */
    public Migration addMigration(String key) {
        Migration migration = new Migration();
        migration.setKey(key);
        migration.setMigrationDate(LocalDateTime.now().format(formatter));
        getEntityManager().persist(migration);
        return migration;
    }

    private EntityManager getEntityManager() {
        return session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

    @Override
    public void close() {
        // nothing here
    }
}
