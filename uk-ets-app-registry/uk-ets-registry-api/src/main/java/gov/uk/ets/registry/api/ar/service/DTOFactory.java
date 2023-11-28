package gov.uk.ets.registry.api.ar.service;

import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.ar.domain.ARUpdateAction;
import gov.uk.ets.registry.api.ar.service.dto.ARUpdateActionDTO;
import gov.uk.ets.registry.api.ar.service.dto.AuthorizedRepresentativeDTO;
import gov.uk.ets.registry.api.ar.service.dto.WorkContactDetailsDTO;
import gov.uk.ets.registry.api.user.UserDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserWorkContact;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Factory for creating data transfer objects.
 */
@Component
public class DTOFactory {
    /**
     * Creates a list of {@link AuthorizedRepresentativeDTO} data transfer objects from a list of {@link AccountAccess} entities.
     *
     * @param accountAccesses The {@link AuthorizedRepresentativeDTO} entities.
     * @return The {@link AuthorizedRepresentativeDTO} data transfer objects.
     */
    public List<AuthorizedRepresentativeDTO> createAuthorizedRepresentativeDTOs(List<AccountAccess> accountAccesses,
                                                                                List<UserWorkContact> userWorkContacts) {
        Map<String, UserWorkContact> personalUserInfos = getWorkContactsAsMap(userWorkContacts);
        return accountAccesses.stream().map(accountAccess -> createAuthorizedRepresentativeDTO(accountAccess,
            personalUserInfos.get(accountAccess.getUser().getUrid()))
        ).collect(Collectors.toList());
    }

    /**
     * Creates a {@link AuthorizedRepresentativeDTO} data transfer object by combining the attributes of a {@link
     * AccountAccess} entity and the attributes of a {@link UserWorkContact} entity
     *
     * @param accountAccess   The {@link AccountAccess} entity.
     * @param userWorkContact The {@link UserWorkContact} entity.
     * @return The {@link AuthorizedRepresentativeDTO} data transfer object.
     */
    public AuthorizedRepresentativeDTO createAuthorizedRepresentativeDTO(AccountAccess accountAccess,
                                                                         UserWorkContact userWorkContact) {
        return AuthorizedRepresentativeDTO
            .builder()
            .right(accountAccess.getRight())
            .state(accountAccess.getState())
            .urid(userWorkContact.getUrid())
            .user(UserDTO.builder()
                .firstName(userWorkContact.getFirstName())
                .lastName(userWorkContact.getLastName())
                .alsoKnownAs(userWorkContact.getAlsoKnownAs())
                .status(accountAccess.getUser().getState())
                .build())
            .contact(getWorkContactDetailsDTO(userWorkContact))
            .build();
    }

    /**
     * Creates a {@link AuthorizedRepresentativeDTO} data transfer object from a {@link UserWorkContact} entity.
     *
     * @param userWorkContact
     * @return
     */
    public AuthorizedRepresentativeDTO createAuthorizedRepresentativeDTO(UserWorkContact userWorkContact) {
        return AuthorizedRepresentativeDTO
            .builder()
            .urid(userWorkContact.getUrid())
            .user(UserDTO.builder()
                .firstName(userWorkContact.getFirstName())
                .lastName(userWorkContact.getLastName())
                .alsoKnownAs(userWorkContact.getAlsoKnownAs())
                .build())
            .contact(getWorkContactDetailsDTO(userWorkContact))
            .build();
    }

    /**
     * Creates a {@link AuthorizedRepresentativeDTO} data transfer object by combining the attributes of a
     * {@link User} entity and the attributes of a {@link UserWorkContact} entity
     *
     * @param userWorkContact The {@link UserWorkContact} entity.
     * @param user            The {@link User} entity.
     * @return
     */
    public AuthorizedRepresentativeDTO createAuthorizedRepresentativeDTO(UserWorkContact userWorkContact, User user) {
        return AuthorizedRepresentativeDTO
            .builder()
            .urid(userWorkContact.getUrid())
            .firstName(userWorkContact.getFirstName())
            .lastName(userWorkContact.getLastName())
            .user(UserDTO.builder()
                .firstName(userWorkContact.getFirstName())
                .lastName(userWorkContact.getLastName())
                .alsoKnownAs(userWorkContact.getAlsoKnownAs())
                .status(user.getState())
                .build())
            .contact(getWorkContactDetailsDTO(userWorkContact))
            .build();
    }

    /**
     * Creates a list of {@link ARUpdateActionDTO} data transfer from a list of {@link ARUpdateAction} entities and a
     * list of {@link UserWorkContact} entities.
     *
     * @param arUpdateActions  The {@link ARUpdateAction} entities.
     * @param userWorkContacts The  {@link UserWorkContact} entities.
     * @return
     */
    public List<ARUpdateActionDTO> createARUpdateActionDTOs(List<ARUpdateAction> arUpdateActions,
                                                            List<UserWorkContact> userWorkContacts) {
        if (CollectionUtils.isEmpty(arUpdateActions)) {
            return new ArrayList<>();
        }
        Map<String, UserWorkContact> userWorkContactsMap = getWorkContactsAsMap(userWorkContacts);
        return arUpdateActions.stream().map(action -> {
                String candidateUrid = action.getUrid();
                UserWorkContact userWorkContact = userWorkContactsMap.get(candidateUrid);
                if (userWorkContact == null) {
                    throw AccountActionException.create(AccountActionError.builder()
                        .code(AccountActionError.USER_NOT_FOUND_IN_KEYCLOAK_DB)
                        .urid(candidateUrid)
                        .message("User with urid " + candidateUrid + " was not found in Keycloak database.")
                        .build());
                }
                return ARUpdateActionDTO.builder()
                    .updateType(action.getType())
                    .candidate(AuthorizedRepresentativeDTO
                        .builder()
                        .right(action.getAccountAccessRight())
                        .urid(userWorkContact.getUrid())
                        .user(UserDTO.builder()
                            .firstName(userWorkContact.getFirstName())
                            .lastName(userWorkContact.getLastName())
                            .alsoKnownAs(userWorkContact.getAlsoKnownAs())
                            .build())
                        .contact(getWorkContactDetailsDTO(userWorkContact))
                        .build())
                    .build();
            }
        ).collect(Collectors.toList());
    }

    /**
     * Returns a map with key the urid of the user and value the user's {@link UserWorkContact} entity.
     *
     * @param userWorkContacts The list of {@link UserWorkContact} entities.
     * @return The urid to {@link UserWorkContact} map.
     */
    private Map<String, UserWorkContact> getWorkContactsAsMap(List<UserWorkContact> userWorkContacts) {
        return userWorkContacts.stream().collect(Collectors.toMap(UserWorkContact::getUrid, Function.identity()));
    }

    /**
     * Creates a {@link WorkContactDetailsDTO} data transfer object from a {@link UserWorkContact} entity.
     *
     * @param userWorkContact The {@link UserWorkContact} entity.
     * @return The {@link WorkContactDetailsDTO} data transfer objects.
     */
    private WorkContactDetailsDTO getWorkContactDetailsDTO(UserWorkContact userWorkContact) {
        return WorkContactDetailsDTO.builder()
            .workBuildingAndStreet(userWorkContact.getWorkBuildingAndStreet())
            .workBuildingAndStreetOptional(userWorkContact.getWorkBuildingAndStreetOptional())
            .workBuildingAndStreetOptional2(userWorkContact.getWorkBuildingAndStreetOptional2())
            .workCountry(userWorkContact.getWorkCountry())
            .workCountryCode(userWorkContact.getWorkCountryCode())
            .workPhoneNumber(userWorkContact.getWorkPhoneNumber())
            .workEmailAddress(userWorkContact.getEmail())
            .workPostCode(userWorkContact.getWorkPostCode())
            .workTownOrCity(userWorkContact.getWorkTownOrCity())
            .workStateOrProvince(userWorkContact.getWorkStateOrProvince())
            .build();
    }
}
