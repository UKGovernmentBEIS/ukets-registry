package gov.uk.ets.keycloak.users.service.adapter.rest;


import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.KeycloakSession;

import gov.uk.ets.keycloak.users.service.application.UkEtsUsersApplicationException;
import gov.uk.ets.keycloak.users.service.application.UkEtsUsersService;
import gov.uk.ets.keycloak.users.service.application.domain.Pageable;
import gov.uk.ets.keycloak.users.service.application.domain.UserPersonalInfo;

/**
 * The Rest controller
 */
public class UkEtsUsersController {

    private UkEtsUsersService service;
    private final KeycloakSession session;
    private UserFilterMapper userFilterMapper;

    public UkEtsUsersController(UkEtsUsersService service, KeycloakSession session) {
        this.service = service;
        this.session = session;
        userFilterMapper = new UserFilterMapper();
    }

    /**
     * Searches for users according to the passed {@link SearchInput} criteria and returns pageable results
     * @param input The search criteria
     * @return The {@link Pageable} of search
     * @throws UkEtsUsersApplicationException
     */
    @GET
    @Path("")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    public Pageable getUsers(@BeanParam SearchInput input) throws UkEtsUsersApplicationException {
        new PermissionsChecker(session).checkPermissions();
        return service.getUsers(userFilterMapper.map(input), session);
    }

    /**
     * Gets the personal info of the users that correspond to the passed list of urid.
     * @param urids The list of urid
     * @return The list of {@link UserPersonalInfo}
     * @throws UkEtsUsersApplicationException
     */
    @GET
    @Path("contacts")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserPersonalInfo> getUserPersonalInfos(@QueryParam("urid") List<String> urids) throws UkEtsUsersApplicationException {
        new PermissionsChecker(session).checkPermissions();
        return service.getUserPersonalInfos(urids, session);
    }
    

    @GET
    @Path("expired")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getNonRegisteredUsersCreatedBefore(@QueryParam("beforeDateTime") Long beforeDateTime) throws UkEtsUsersApplicationException {
        new PermissionsChecker(session).checkPermissions();
        return service.fetchNonRegisteredUsersCreatedBefore(beforeDateTime , session);
    }
}
