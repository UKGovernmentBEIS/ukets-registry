package gov.uk.ets.registry.api.notification.userinitiated;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.ErrorDetail;
import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.file.upload.dto.NotificationValidationRequest;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationDefinitionRepository;
import gov.uk.ets.registry.api.notification.userinitiated.services.NotificationValidationService;
import gov.uk.ets.registry.api.notification.userinitiated.services.UserInitiatedNotificationService;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.NotificationGenerator;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationDTO;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationSearchPageableMapper;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationSearchResult;
import gov.uk.ets.registry.api.user.service.UserService;
import jakarta.servlet.annotation.WebFilter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// excludes our custom filters (like CustomLoggingFilter etc.)
@WebMvcTest(controllers = NotificationController.class, excludeFilters = @ComponentScan.Filter(WebFilter.class))
// disables all spring security (and probably other filters too) filters
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("integrationTest")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserInitiatedNotificationService userInitiatedNotificationService;

    @MockBean
    private NotificationValidationService notificationValidationService;

    @MockBean
    private NotificationGenerator anyGenerator;

    @MockBean
    private NotificationSearchPageableMapper pageableMapper;

    @MockBean
    private UserService userService;

    @MockBean
    BuildProperties buildProperties;

    @MockBean
    private NotificationDefinitionRepository notificationDefinitionRepository;

    @MockBean
    private AuthorizationService authorizationService;

    @Test
    void shouldFailWithInvalidRequest() throws Exception {
        NotificationDTO notificationRequest = NotificationDTO.builder()
            .build();
        MvcResult result = this.mockMvc.perform(
                post("/api-registry/notifications.create")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(notificationRequest))
            ).
            andExpect(status().isBadRequest())
            .andReturn();

        ErrorBody error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorBody.class);

        assertThat(error).isNotNull();
        assertThat(error.getErrorDetails()).hasSize(3);
        assertThat(error.getErrorDetails()).extracting(ErrorDetail::getMessage).containsOnly(
            "contentDetails The notification content details are mandatory",
            "activationDetails The notification activation details are mandatory",
            "type The notification type is mandatory"
        );
    }

    @Test
    void test() throws Exception {
        PageParameters pageParameters = new PageParameters();
        pageParameters.setPage(0);
        pageParameters.setPageSize(10L);
        pageParameters.setSortField("scheduledDate");
        pageParameters.setSortDirection(Sort.Direction.DESC);

        Page<NotificationSearchResult> searchResults = Mockito.mock(Page.class);
        Mockito.when(userInitiatedNotificationService.search(any(), any())).thenReturn(searchResults);

        mockMvc.perform(get("/api-registry/notifications.list")
                .param("page", "0")
                .param("pageSize", "10")
                .param("sortField", "scheduledDate")
                .param("sortDirection", "DESC")
                .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());
    }

    @Test
    void validateEmailNotification_shouldReturnNoContent() throws Exception {
        NotificationValidationRequest request = NotificationValidationRequest.builder()
            .body("body with no placeholders")
            .type(NotificationType.EMISSIONS_MISSING_FOR_AOHA)
            .build();

        Mockito.when(anyGenerator.generate(any(), anyString(), anyString())).thenReturn(any());

        mockMvc.perform(post("/api-registry/notifications.validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andExpect(status().isNoContent());
    }
}
