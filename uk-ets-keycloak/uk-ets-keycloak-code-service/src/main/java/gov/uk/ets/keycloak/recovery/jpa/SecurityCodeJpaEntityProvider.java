package gov.uk.ets.keycloak.recovery.jpa;

import java.util.Collections;
import java.util.List;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;

public class SecurityCodeJpaEntityProvider implements JpaEntityProvider {
    @Override
    public List<Class<?>> getEntities() {
        return Collections.singletonList(SecurityCode.class);

    }

    @Override
    public String getChangelogLocation() {
        return "META-INF/liquibase/security-code-changelog_v_0_0_1.xml";
    }

    @Override
    public String getFactoryId() {
        return SecurityCodeJpaEntityProviderFactory.ID;
    }

    @Override
    public void close() {
        // nothing here
    }
}
