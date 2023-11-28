package gov.uk.ets.registry.api.user.admin;

import gov.uk.ets.registry.api.user.admin.service.AuthorityAdministrationService;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsUpdateDTO;
import gov.uk.ets.registry.api.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserAdministrationControllerTest {
    
    @InjectMocks
    private UserAdministrationController controller;

    @Mock
    private AuthorityAdministrationService authorityAdministrationService;
    
    @Mock
    private UserAdministrationService userAdministrationService;
    
    @Mock
    private UserService userService;

    @Test
    void getEnrolledUser() {
        // given
        String urid = "test-urid";
        // when
        controller.getEnrolledUser(urid);
        //then
        then(authorityAdministrationService).should(times(1)).getEnrolledUser(eq(urid));
    }

    @Test
    void addAuthorityUser() {
        // given
        String urid = "test-urid";
        // when
        controller.addAuthorityUser(urid);
        //then
        then(authorityAdministrationService).should(times(1)).setAuthorityUser(eq(urid));
    }

    @Test
    void removeUserFromAuthorityUsers() {
        // given
        String urid = "test-urid";
        // when
        controller.removeUserFromAuthorityUsers(urid);
        //then
        then(authorityAdministrationService).should(times(1)).removeUserFromAuthorityUsers(eq(urid));
    }

    @Test
    @DisplayName("Update user details phone number")
    void updateUserPhoneNumber() throws Exception {
        // given
        UserDetailsUpdateDTO dto = new UserDetailsUpdateDTO();
        UserDetailsDTO diff = new UserDetailsDTO();
        diff.setFirstName("FIRST");
        diff.setLastName("LAST");
        diff.setWorkPhoneNumber("argargharh");
        diff.setWorkCountryCode("argargharh");
        diff.setCountryOfBirth("UK");
        dto.setDiff(diff);
        UserDetailsDTO current = new UserDetailsDTO();
        current.setFirstName("FIRSTNAME");
        current.setLastName("LASTNAME");
        current.setCountryOfBirth("UK");
        dto.setCurrent(current);
        String urid = "urid";

        controller.updateUserDetails(urid, dto);
    }
    
    @Test
    @DisplayName("Major fields are changed to the user profile")
    void updateMajorUserDetails() throws Exception {
        // given
        UserDetailsUpdateDTO dto = new UserDetailsUpdateDTO();
        UserDetailsDTO diff = new UserDetailsDTO();
        diff.setFirstName("FIRST");
        diff.setLastName("LAST");
        diff.setCountryOfBirth("UK");
        dto.setDiff(diff);
        UserDetailsDTO current = new UserDetailsDTO();
        current.setFirstName("FIRSTNAME");
        current.setLastName("LASTNAME");
        current.setCountryOfBirth("UK");
        dto.setCurrent(current);
        String urid = "urid";
        
        // when
        controller.updateUserDetails(urid, dto);
        //then
        then(userService).should(times(1)).submitMajorUserDetailsUpdateRequest(urid, dto);
        then(userService).should(times(0)).submitMinorUserDetailsUpdateRequest(urid, dto);
    }
    
    @Test
    @DisplayName("only minor fields are changed to the user profile")
    void updateMinorUserDetails() throws Exception {
        // given
        UserDetailsUpdateDTO dto = new UserDetailsUpdateDTO();
        UserDetailsDTO diff = new UserDetailsDTO();
        diff.setWorkBuildingAndStreet("Feel good");
        diff.setWorkBuildingAndStreetOptional("Make my day");
        diff.setWorkBuildingAndStreetOptional2("Twelve monkeys");
        diff.setWorkTownOrCity("Peristeri");
        diff.setWorkCountry("Athena");
        diff.setWorkPostCode("12136");
        dto.setDiff(diff);
        UserDetailsDTO current = new UserDetailsDTO();
        current.setFirstName("FIRSTNAME");
        current.setLastName("LASTNAME");
        current.setCountryOfBirth("UK");
        dto.setCurrent(current);
        String urid = "urid";
        
        // when
        controller.updateUserDetails(urid, dto);
        //then
        then(userService).should(times(0)).submitMajorUserDetailsUpdateRequest(urid, dto);
        then(userService).should(times(1)).submitMinorUserDetailsUpdateRequest(urid, dto);
    }
    
    @Test
    @DisplayName("Major and Minor fields are changed to the user profile")
    void updateMajorAndMinorUserDetails() throws Exception {
        // given
        UserDetailsUpdateDTO dto = new UserDetailsUpdateDTO();
        UserDetailsDTO diff = new UserDetailsDTO();
        diff.setFirstName("FIRST");
        diff.setLastName("LAST");
        diff.setCountryOfBirth("UK");
        diff.setWorkPostCode("12136");
        dto.setDiff(diff);
        UserDetailsDTO current = new UserDetailsDTO();
        current.setFirstName("FIRSTNAME");
        current.setLastName("LASTNAME");
        current.setCountryOfBirth("UK");
        dto.setCurrent(current);
        String urid = "urid";
        
        // when
        controller.updateUserDetails(urid, dto);
        //then
        then(userService).should(times(1)).submitMajorUserDetailsUpdateRequest(urid, dto);
        then(userService).should(times(0)).submitMinorUserDetailsUpdateRequest(urid, dto);
    }
}