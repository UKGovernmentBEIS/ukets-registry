package uk.gov.ets.password.validator.api.web.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


public class ValidatePasswordResponseTest {
	
    @Test
    public void testEquals() {
    	ValidatePasswordResponse response_1 = new ValidatePasswordResponse();
    	response_1.setValid(false);
    	response_1.setErrorCode("erroCode");
    	response_1.setMessage("pwned");
        
        ValidatePasswordResponse response_2 = new ValidatePasswordResponse();
        response_2.setValid(false);
        response_2.setErrorCode("erroCode");
        response_2.setMessage("pwned");
       
        
        assertEquals(response_1,response_2);
    }
}
