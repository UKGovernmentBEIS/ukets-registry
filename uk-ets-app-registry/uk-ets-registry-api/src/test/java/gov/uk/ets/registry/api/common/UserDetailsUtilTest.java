package gov.uk.ets.registry.api.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsUpdateDTO;

public class UserDetailsUtilTest {

    private static final String TEST_URID = "UK12345678";

    @Test
    @DisplayName("Check User Details Update Comment upon change on minor fields.")
    void testUserDetailsUpdateComments_minorFields() {
    	
    	UserDetailsDTO diff = new UserDetailsDTO();
    	diff.setWorkBuildingAndStreet("Feel good street");
    	diff.setWorkBuildingAndStreetOptional("Make my day");
    	diff.setWorkBuildingAndStreetOptional2("Twelve monkeys");
    	diff.setWorkTownOrCity("Peristeri");
    	diff.setWorkCountry("Athena");
    	diff.setWorkPostCode("12136");
	    
	    UserDetailsUpdateDTO dto = new UserDetailsUpdateDTO();
	    dto.setDiff(diff);
	    UserDetailsDTO current = new UserDetailsDTO();
	    current.setUsername("UserName");
	    current.setFirstName("firstName");
	    current.setLastName("SurName");
	    current.setCountryOfBirth("UK");
	    current.setWorkBuildingAndStreet("Bad Day");
	    dto.setCurrent(current);
    	
    	String comment = UserDetailsUtil.generateUserDetailsUpdateComment(dto.getCurrent(), dto.getDiff());
		assertThat(comment).contains("(Work)Postal Code or ZIP change from - to 12136")
				.contains("(Work)Address 1 change from Bad Day to Feel good street")
				.contains("(Work)Address 2 change from - to Make my day")
				.contains("(Work)Address 3 change from - to Twelve monkeys")
				.contains("(Work)Town or City change from - to Peristeri")
				.contains("(Work)Country change from - to Athena");
       
    }
    
    @Test
    @DisplayName("Check User Details Update Comment upon change on fistName.")
    void testUserDetailsUpdateComments_FirstName() {
    	
    	UserDetailsDTO diff = new UserDetailsDTO();
    	diff.setFirstName("first");
	    
	    UserDetailsUpdateDTO dto = new UserDetailsUpdateDTO();
	    dto.setDiff(diff);
	    UserDetailsDTO current = new UserDetailsDTO();
	    current.setUsername("UserName");
	    current.setFirstName("firstName");
	    current.setLastName("SurName");
	    current.setCountryOfBirth("UK");
	    current.setWorkBuildingAndStreet("Bad Day");
	    dto.setCurrent(current);
    	
    	String comment = UserDetailsUtil.generateUserDetailsUpdateComment(dto.getCurrent(), dto.getDiff());
    	assertThat(comment).contains("First and middle names change from firstName to first");
        
    }
    
    @Test
    @DisplayName("Check User Details Update Comment upon change on fistName.")
    void testUserDetailsUpdateComments_FirstNamed() {
    	
    	UserDetailsDTO diff = new UserDetailsDTO();
    	diff.setFirstName("");
	    
	    UserDetailsUpdateDTO dto = new UserDetailsUpdateDTO();
	    dto.setDiff(diff);
	    UserDetailsDTO current = new UserDetailsDTO();
	    current.setUsername("UserName");
	    current.setFirstName("firstName");
	    current.setLastName("SurName");
	    current.setCountryOfBirth("UK");
	    current.setWorkBuildingAndStreet("Bad Day");
	    dto.setCurrent(current);
    	
    	String comment = UserDetailsUtil.generateUserDetailsUpdateComment(dto.getCurrent(), dto.getDiff());
    	assertThat(comment).contains("First and middle names change from firstName to -");
        
    }
    
    @Test
    @DisplayName("Verify minor change when only minor fields are changed to the user profile.")
    void test_minorUserDetailsUpdateRequest() {
    	// given
        UserDetailsUpdateDTO dto = new UserDetailsUpdateDTO();
        UserDetailsDTO changed = new UserDetailsDTO();
        changed.setWorkBuildingAndStreet("Feel good street");
        changed.setWorkBuildingAndStreetOptional("Make my day");
        changed.setWorkBuildingAndStreetOptional2("Twelve monkeys");
        changed.setWorkTownOrCity("Peristeri");
        changed.setWorkCountry("Athena");
        changed.setWorkPostCode("12136");
        dto.setDiff(changed);
        UserDetailsDTO current = new UserDetailsDTO();
        current.setFirstName("FIRSTNAME");
        current.setLastName("LASTNAME");
        current.setCountryOfBirth("UK");
        current.setUrid(TEST_URID);
        dto.setCurrent(current);

        assertFalse(UserDetailsUtil.majorUserDetailsUpdateRequested(changed));
    }
    
    @Test
    @DisplayName("Verify major change when only major fields are changed to the user profile.")
    void test_majorUserDetailsUpdateRequest() {
    	// given
        UserDetailsUpdateDTO dto = new UserDetailsUpdateDTO();
        UserDetailsDTO changed = new UserDetailsDTO();
        changed.setFirstName("FIRSTNAME");
        dto.setDiff(changed);
        UserDetailsDTO current = new UserDetailsDTO();
        current.setFirstName("FIRST");
        current.setLastName("LASTNAME");
        current.setCountryOfBirth("UK");
        current.setUrid(TEST_URID);
        dto.setCurrent(current);

        assertTrue(UserDetailsUtil.majorUserDetailsUpdateRequested(changed));
    }
    
    @Test
    @DisplayName("Verify major change when major and minor fields are changed to the user profile.")
    void test_majorAndMinorUserDetailsUpdateRequest() {
    	// given
        UserDetailsUpdateDTO dto = new UserDetailsUpdateDTO();
        UserDetailsDTO changed = new UserDetailsDTO();
        changed.setFirstName("FIRSTNAME");
        changed.setWorkBuildingAndStreet("Feel good street");
        dto.setDiff(changed);
        UserDetailsDTO current = new UserDetailsDTO();
        current.setFirstName("FIRST");
        current.setLastName("LASTNAME");
        current.setCountryOfBirth("UK");
        current.setUrid(TEST_URID);
        dto.setCurrent(current);
        
        assertTrue(UserDetailsUtil.majorUserDetailsUpdateRequested(changed));
    }
}
