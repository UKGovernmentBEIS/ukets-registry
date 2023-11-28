package gov.uk.ets.registry.api.user.forgot.passwd;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.uk.ets.registry.api.common.web.UkEtsExceptionControllerAdvice;
import gov.uk.ets.registry.api.user.forgot.passwd.service.ForgotPasswordService;
import gov.uk.ets.registry.api.user.forgot.passwd.web.ResetPasswordRequest;
import gov.uk.ets.registry.api.user.forgot.passwd.web.ResetPasswordResponse;

@ExtendWith(MockitoExtension.class)
public class ForgotPasswordControllerTest {

    private MockMvc mockMvc;

    private ForgotPasswordController controller;

    @Mock
    private ForgotPasswordService forgotPasswordService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        controller = new ForgotPasswordController(forgotPasswordService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new UkEtsExceptionControllerAdvice()).build();
    }

    @Test
    public void testResetPasswordSuccessReponse() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest("token","123456","12345678newPasswd");
        ResetPasswordResponse response = new ResetPasswordResponse(true,"test.reset@passwd.com");
        Mockito.when(forgotPasswordService.resetPassword(request)).thenReturn(response);
        
        mockMvc.perform(post("/api-registry/forgot-password.reset.password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.*", hasSize(2)))
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.email", is(response.getEmail())));
    }
    
    @Test
    public void testResetPasswordFailureEmptyPasswd() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest("token","123456",null);
        
        mockMvc.perform(post("/api-registry/forgot-password.reset.password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetails", hasSize(1)))
            .andExpect(jsonPath("$.errorDetails[0].message", is("newPasswd must not be null")));
    }
    
    @Test
    public void testResetPasswordFailurePasswdSizeLessThan8() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest("token","123456","123");
        
        mockMvc.perform(post("/api-registry/forgot-password.reset.password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetails", hasSize(1)))
            .andExpect(jsonPath("$.errorDetails[0].message", is("newPasswd Password must be at least 8 characters long.")));
    }
    
    @Test
    public void testResetPasswordFailureInvalidOTP() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest("token","anTOTP","12345678");
        
        mockMvc.perform(post("/api-registry/forgot-password.reset.password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetails", hasSize(1)))
            .andExpect(jsonPath("$.errorDetails[0].message", is("otp OTP code consists of 6 numbers.")));
    }
    
    @Test
    public void testResetPasswordFailureEmptyToken() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest(null,"123456","12345678");
        
        mockMvc.perform(post("/api-registry/forgot-password.reset.password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetails", hasSize(1)))
            .andExpect(jsonPath("$.errorDetails[0].message", is("token Token cannot be empty.")));
    }
}
