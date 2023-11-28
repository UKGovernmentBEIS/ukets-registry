/**
 * 
 */
package uk.gov.ets.registration.user;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.ets.registration.user.exception.UserRegistrationException;
import uk.gov.ets.registration.user.service.UserRegistrationService;

@SpringBootTest
@AutoConfigureMockMvc
class UserRegistrationControllerIntegrationTest {

	public static final String REQUEST_MAPPING_PATH = "/api-registration";
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserRegistrationService service;

    /**
     * Verify that the user registration process returns Http status CREATED.
     */
    @Test
    void when_registerUser_shouldReturnHttpStatusCreated() throws Exception {
        ObjectMapper mapper =new ObjectMapper();
        final String email = "somebody@somewhere.com";
        UserRegisterParams params = new UserRegisterParams();
        params.setEmail(email);

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setAttributes(new HashMap<>());

        when(service.findByEmail(params.getEmail())).thenReturn(Optional.of(userRepresentation));

        mockMvc.perform(post(REQUEST_MAPPING_PATH)
                .content(mapper.writeValueAsString(params))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
//                .andDo(print())
                .andExpect(status().isCreated());
    }
	
    
    @Test
    void when_registerUser_ErrorShouldReturnHttpStatusBadRequest() throws Exception {
        ObjectMapper mapper =new ObjectMapper();
        final String email = "somebody@somewhere.com";
        UserRegisterParams params = new UserRegisterParams();
        params.setEmail(email);
        
        doThrow(new UserRegistrationException(HttpStatus.INTERNAL_SERVER_ERROR.value())).when(service).registerUser(Mockito.eq(params.getEmail()));        
        
        mockMvc.perform(post(REQUEST_MAPPING_PATH)
                .content(mapper.writeValueAsString(params))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
//                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void when_registerUserEmptyEmail_ErrorShouldReturnHttpStatusBadRequest() throws Exception {
        ObjectMapper mapper =new ObjectMapper();
        final String email = "";
        UserRegisterParams params = new UserRegisterParams();
        params.setEmail(email);
                
        mockMvc.perform(post(REQUEST_MAPPING_PATH)
                .content(mapper.writeValueAsString(params))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
//                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void when_registerUserInvalidEmail_ErrorShouldReturnHttpStatusBadRequest() throws Exception {
        ObjectMapper mapper =new ObjectMapper();
        final String email = "zzz";
        UserRegisterParams params = new UserRegisterParams();
        params.setEmail(email);
                
        mockMvc.perform(post(REQUEST_MAPPING_PATH)
                .content(mapper.writeValueAsString(params))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
//                .andDo(print())
                .andExpect(status().isBadRequest());
    }    
    
    @Test
    void when_verifyUserEmail_shouldReturnHttpStatusOk() throws Exception {
        final String email = "somebody@somewhere.com";
        final String id = "615f4fae-7352-4048-be62-67b0a5186fbc";
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(id);
        userRepresentation.setUsername(email);
        userRepresentation.setEmail(email);
        userRepresentation.setEmailVerified(false);
        userRepresentation.setEnabled(true);
        userRepresentation.setRequiredActions(Arrays.asList(UserRegistrationService.VERIFY_EMAIL_REQUIRED_ACTION,
            UserRegistrationService.CONFIGURE_OTP_REQUIRED_ACTION));
        userRepresentation.setAttributes(new HashMap<>());
        userRepresentation.getAttributes().put(UserAttributes.URID.getName(), Collections.singletonList("UK24044832156"));
        userRepresentation.getAttributes().put(UserAttributes.REGISTRATION_IN_PROGRESS.getName(), Collections.singletonList(Boolean.TRUE.toString()));

        when(service.findById(Mockito.eq(id))).thenReturn(userRepresentation);
        when(service.update(userRepresentation)).thenReturn(userRepresentation);
        when(service.validateToken(Mockito.anyString())).thenReturn(id);

        List<String> requiredActions = new ArrayList<>();
        requiredActions.add(UserRegistrationService.CONFIGURE_OTP_REQUIRED_ACTION);
        // requiredActions.add(KeycloakService.TERMS_AND_CONDITIONS_ACTION);

        mockMvc.perform(patch(REQUEST_MAPPING_PATH + "/{id}",id)
//                .content(mapper.writeValueAsString(params))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.username", is(email)))
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.enabled", is(true)))
                .andExpect(jsonPath("$.requiredActions",is(requiredActions)))
                .andExpect(jsonPath("$.emailVerified", is(true)));
    }

    @Test
    void when_register_and_registration_is_in_progress_shouldReturnHttpStatusOkAndNonEmptyBody() throws Exception {
        final String email = "somebody@somewhere.com";
        final String id = "615f4fae-7352-4048-be62-67b0a5186fbc";
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(id);
        userRepresentation.setUsername(email);
        userRepresentation.setEmail(email);
        userRepresentation.setAttributes(new HashMap<>());
        userRepresentation.getAttributes().put(UserAttributes.REGISTRATION_IN_PROGRESS.getName(), Collections.singletonList(Boolean.TRUE.toString()));

        when(service.findById(Mockito.eq(id))).thenReturn(userRepresentation);

        mockMvc.perform(get(REQUEST_MAPPING_PATH + "/" + id)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").exists());
    }

    @Test
    void when_register_and_registration_is_not_in_progress_shouldReturnHttpStatusOkAndEmptyBody() throws Exception {
        final String email = "somebody@somewhere.com";
        final String id = "615f4fae-7352-4048-be62-67b0a5186fbc";
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(id);
        userRepresentation.setUsername(email);
        userRepresentation.setEmail(email);
        userRepresentation.setAttributes(new HashMap<>());

        when(service.findById(Mockito.eq(id))).thenReturn(userRepresentation);

        mockMvc.perform(get(REQUEST_MAPPING_PATH + "/" + id)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void when_delete_and_registration_is_in_progress_shouldReturnHttpStatusBadRequest() throws Exception {
        final String email = "somebody@somewhere.com";
        final String id = "615f4fae-7352-4048-be62-67b0a5186fbc";
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(id);
        userRepresentation.setUsername(email);
        userRepresentation.setEmail(email);
        userRepresentation.setAttributes(new HashMap<>());

        userRepresentation.getAttributes().put(UserAttributes.REGISTRATION_IN_PROGRESS.getName(), Arrays.asList(Boolean.TRUE.toString()));

        when(service.findById(Mockito.eq(id))).thenReturn(userRepresentation);

        mockMvc.perform(delete(REQUEST_MAPPING_PATH )
                .param("userId", id)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.ALL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void when_delete_and_registration_is_not_in_progress_but_with_threshold_60_sec_shouldReturnHttpStatusOk() throws Exception {
        final String email = "somebody@somewhere.com";
        final String id = "615f4fae-7352-4048-be62-67b0a5186fbc";
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(id);
        userRepresentation.setUsername(email);
        userRepresentation.setEmail(email);
        userRepresentation.setAttributes(new HashMap<>());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registered = now.minusSeconds(60);

        userRepresentation.getAttributes().put(UserAttributes.REGISTERED_ON_DATE.getName(),
                List.of(registered.format(DateTimeFormatter.ISO_DATE_TIME)));

        when(service.findById(Mockito.eq(id))).thenReturn(userRepresentation);

        mockMvc.perform(delete(REQUEST_MAPPING_PATH )
                .param("userId", id)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void when_delete_and_registration_is_not_in_progress_but_with_threshold_60_sec_shouldReturnHttpStatusBadRequest() throws Exception {
        final String email = "somebody@somewhere.com";
        final String id = "615f4fae-7352-4048-be62-67b0a5186fbc";
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(id);
        userRepresentation.setUsername(email);
        userRepresentation.setEmail(email);
        userRepresentation.setAttributes(new HashMap<>());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime registered = now.minusSeconds(61);

        userRepresentation.getAttributes().put(UserAttributes.REGISTERED_ON_DATE.getName(),
                List.of(registered.format(DateTimeFormatter.ISO_DATE_TIME)));

        when(service.findById(Mockito.eq(id))).thenReturn(userRepresentation);

        mockMvc.perform(delete(REQUEST_MAPPING_PATH )
                .param("userId", id)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.ALL))
                .andExpect(status().isBadRequest());
    }
}
