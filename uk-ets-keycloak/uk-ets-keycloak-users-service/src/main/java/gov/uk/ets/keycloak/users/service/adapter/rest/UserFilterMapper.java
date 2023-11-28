package gov.uk.ets.keycloak.users.service.adapter.rest;

import static java.util.stream.Collectors.toList;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import gov.uk.ets.keycloak.users.service.application.UkEtsUsersApplicationException;
import gov.uk.ets.keycloak.users.service.application.domain.UserFilter;
import gov.uk.ets.keycloak.users.service.infrastructure.Constants;
import gov.uk.ets.keycloak.users.service.infrastructure.UserStatus;


/**
 * Maps the rest endpoint parameters to a {@link UserFilter} domain value object
 */
public class UserFilterMapper {
    public UserFilter map(SearchInput input) throws UkEtsUsersApplicationException {
        LocalDateTime lastSignInFrom = null;
        if (input.getLastSignInFrom() != null) {
            LocalDate localDate = LocalDate.parse(input.getLastSignInFrom(), DateTimeFormatter.ISO_DATE);
            lastSignInFrom = localDate.atStartOfDay();
        }
        LocalDateTime lastSignInTo = null;
        if (input.getLastSignInTo() != null) {
            LocalDate localDate = LocalDate.parse(input.getLastSignInTo(), DateTimeFormatter.ISO_DATE);
            lastSignInTo = localDate.plusDays(1L).atStartOfDay();
        }
        List<String> roles = null;
        List<String> excludedRoles = null;
        if (input.getRole() != null) {
            Optional<UserRolesMapper> roleMapper = UserRolesMapper.get(input.getRole());
            if (roleMapper.isPresent()) {
                roles = roleMapper.get().keycloakRoles;
                excludedRoles = roleMapper.get().excludedRoles;
            }
        }
        
        return UserFilter.builder()
            .nameOrUserId(urlDecode(input.getNameOrUserId()))
            .email(urlDecode(input.getEmail()))
            .roles(roles)
            .excludedRoles(excludedRoles)
            .lastSignInFrom(lastSignInFrom)
            .lastSignInTo(lastSignInTo)
            .statuses(UserStatusMapper.map(input.getStatus()))
            .sortField(input.getSortField())
            .sortingDirection(input.getSortDirection())
            .page(input.getPage())
            .pageSize(input.getPageSize())
            .build();
    }

    private String urlDecode(String param) throws UkEtsUsersApplicationException {
        try {
            return param != null ? URLDecoder.decode(param, StandardCharsets.UTF_8.name()) : null;
        } catch (UnsupportedEncodingException exception) {
            throw new UkEtsUsersApplicationException(exception);
        }
    }

    private enum UserRolesMapper {
        USER(
                "USER",
                null,
                // excluded role ones
                List.of(
                    Constants.SENIOR_REGISTRY_ADMINISTRATOR,
                    Constants.JUNIOR_REGISTRY_ADMINISTRATOR,
                    Constants.READONLY_REGISTRY_ADMINISTRATOR,
                    Constants.SYSTEM_ADMINISTRATOR,
                    Constants.AUTHORISED_REPRESENTATIVE,
                    Constants.AUTHORITY_USER
                )
        ),
        SENIOR_REGISTRY_ADMINISTRATOR(
                "SENIOR_ADMIN",
                 List.of(Constants.SENIOR_REGISTRY_ADMINISTRATOR),
                null
        ),
        JUNIOR_REGISTRY_ADMINISTRATOR(
                "JUNIOR_ADMIN",
                 List.of(Constants.JUNIOR_REGISTRY_ADMINISTRATOR),
                null
        ),
        READONLY_REGISTRY_ADMINISTRATOR(
                "READONLY_ADMIN",
                 List.of(Constants.READONLY_REGISTRY_ADMINISTRATOR),
                null
        ),
        SYSTEM_ADMINISTRATOR(
                "SYSTEM_ADMINISTRATOR",
                 List.of(Constants.SYSTEM_ADMINISTRATOR),
                null
        ),
        AUTHORITY_USER(
                "AUTHORITY_USER",
                List.of(Constants.AUTHORITY_USER),
                null
        ),
        AUTHORISED_REPRESENTATIVE(
                "AUTHORISED_REPRESENTATIVE",
                 List.of(Constants.AUTHORISED_REPRESENTATIVE),
                null
        );

        private List<String> keycloakRoles;
        private List<String> excludedRoles;
        private String searchParamValue;

        UserRolesMapper(String searchParamValue, List<String> keycloakRoles, List<String> excludedRoles) {
            this.keycloakRoles = keycloakRoles;
            this.searchParamValue = searchParamValue;
            this.excludedRoles = excludedRoles;
        }

        public static Optional<UserRolesMapper> get(String key) {
            return Stream.of(UserRolesMapper.values())
                .filter(userRoleMapper -> userRoleMapper.searchParamValue.equals(key))
                .findFirst();
        }

    }
    
	private static class UserStatusMapper {
		public static final String ALL_EXCEPT_DEACTIVATED = "ALL_EXCEPT_DEACTIVATED";

		public static List<String> map(String status) {
			List<String> userStatusCriteria = null;
			if (ALL_EXCEPT_DEACTIVATED.equals(status)) {
				userStatusCriteria = UserStatus.getAllExceptDeactivated()
						                       .stream()
						                       .map(Enum::name)
						                       .collect(toList());
			} else if(UserStatus.parse(status) != null) {
				userStatusCriteria = List.of(status);
			}
			return userStatusCriteria;
		}
	}
}
