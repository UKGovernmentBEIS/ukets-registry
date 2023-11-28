package gov.uk.ets.keycloak.migration.jpa;

import java.util.Collections;
import java.util.List;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;

public class MigrationJpaEntityProvider implements JpaEntityProvider {
    @Override
    public List<Class<?>> getEntities() {
        return Collections.singletonList(Migration.class);

    }

    @Override
    public String getChangelogLocation() {
        return "META-INF/liquibase/migration-changelog_v_0_0_1.xml";
    }

    @Override
    public String getFactoryId() {
        return MigrationJpaEntityProviderFactory.ID;
    }

    @Override
    public void close() {
        // nothing here
    }
}
