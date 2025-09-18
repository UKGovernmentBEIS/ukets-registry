package gov.uk.ets.keycloak.users.service.infrastructure;

import gov.uk.ets.keycloak.users.service.adapter.persistence.KeycloakUkEtsUsersRepository;
import gov.uk.ets.keycloak.users.service.adapter.persistence.UkEtsUserPersonalInfoRepository;
import gov.uk.ets.keycloak.users.service.adapter.rest.UkEtsUsersController;
import gov.uk.ets.keycloak.users.service.application.UkEtsUsersService;
import gov.uk.ets.keycloak.users.service.application.domain.UkEtsUsersRepository;
import gov.uk.ets.keycloak.users.service.application.UkEtsUsersServiceImpl;
import gov.uk.ets.keycloak.users.service.application.domain.UserPersonalInfoRepository;
import jakarta.persistence.EntityManager;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;

import org.keycloak.services.resource.RealmResourceProvider;

/**
 * {@link RealmResourceProvider} for searching users
 */
public class UserSearchResourceProvider implements RealmResourceProvider {
    private UkEtsUsersController controller;

    public UserSearchResourceProvider(KeycloakSession session) {
        EntityManager entityManager = session.getProvider(JpaConnectionProvider.class).getEntityManager();
        UkEtsUsersRepository repository = new KeycloakUkEtsUsersRepository(entityManager);
        UserPersonalInfoRepository userPersonalInfoRepository = new UkEtsUserPersonalInfoRepository(entityManager);
        UkEtsUsersService ukEtsUsersService = new UkEtsUsersServiceImpl(repository, userPersonalInfoRepository);
        controller = new UkEtsUsersController(ukEtsUsersService, session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getResource() {
        return controller;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // NOOP
    }
}
