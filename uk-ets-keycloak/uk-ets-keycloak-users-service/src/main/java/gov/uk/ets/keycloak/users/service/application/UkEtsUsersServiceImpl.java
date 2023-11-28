package gov.uk.ets.keycloak.users.service.application;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;

import gov.uk.ets.keycloak.logger.CustomLogger;
import gov.uk.ets.keycloak.users.service.application.domain.Pageable;
import gov.uk.ets.keycloak.users.service.application.domain.UkEtsUsersRepository;
import gov.uk.ets.keycloak.users.service.application.domain.UserFilter;
import gov.uk.ets.keycloak.users.service.application.domain.UserPersonalInfo;
import gov.uk.ets.keycloak.users.service.application.domain.UserPersonalInfoRepository;

/**
 * The {@link UkEtsUsersService} implementation.
 */
public class UkEtsUsersServiceImpl implements UkEtsUsersService {
    private UkEtsUsersRepository usersRepository;
    private UserPersonalInfoRepository userPersonalInfoRepository;

    public UkEtsUsersServiceImpl(UkEtsUsersRepository usersRepository,
        UserPersonalInfoRepository userPersonalInfoRepository) {
        this.usersRepository = usersRepository;
        this.userPersonalInfoRepository = userPersonalInfoRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable getUsers(UserFilter filter, KeycloakSession session) throws UkEtsUsersApplicationException {
        try {
            return usersRepository.fetchUsers(filter);
        } catch (Exception throwable) {
            CustomLogger.print(Logger.Level.ERROR, session.getContext().getRequestHeaders().getHeaderString("X-Request-ID"), null, null,
                    null, throwable.getMessage(), throwable);
            throw new UkEtsUsersApplicationException(throwable);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserPersonalInfo> getUserPersonalInfos(List<String> urids, KeycloakSession session) throws UkEtsUsersApplicationException {
        if(urids == null || urids.isEmpty()) {
            UkEtsUsersApplicationException exception = new UkEtsUsersApplicationException("urids parameter is mandatory");
            CustomLogger.print(Logger.Level.ERROR, getXRequestId(session), null, null,
                    null, "urids parameter is mandatory", exception);
            throw exception;
        }
        try {
            return userPersonalInfoRepository.fetchUserPersonalInfos(urids);
        } catch (Exception throwable) {
            CustomLogger.print(Logger.Level.ERROR, getXRequestId(session), null, null,
                    null, throwable.getMessage(), throwable);
            throw new UkEtsUsersApplicationException(throwable);
        }
    }

    private String getXRequestId(KeycloakSession session) {
        return session.getContext().getRequestHeaders().getHeaderString("X-Request-ID");
    }

    @Override
    public List<String> fetchNonRegisteredUsersCreatedBefore(Long beforeDateTime, KeycloakSession session) throws UkEtsUsersApplicationException {
        try {
            return usersRepository.fetchNonRegisteredUsersCreatedBefore(beforeDateTime)
                .stream()
                .map(u -> u.getId())
                .collect(Collectors.toList());
        } catch (Exception throwable) {
            CustomLogger.print(Logger.Level.ERROR, session.getContext().getRequestHeaders().getHeaderString("X-Request-ID"), null, null,
                    null, throwable.getMessage(), throwable);
            throw new UkEtsUsersApplicationException(throwable);
        }
    }
    
}
