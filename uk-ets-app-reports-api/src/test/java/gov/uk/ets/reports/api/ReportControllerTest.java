package gov.uk.ets.reports.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.reports.api.authz.AuthorizationService;
import gov.uk.ets.reports.api.error.ErrorBody;
import gov.uk.ets.reports.api.roleaccess.service.ReportTypesPerRoleService;
import gov.uk.ets.reports.api.web.model.ReportAddRemoveRoleRequest;
import gov.uk.ets.reports.api.web.model.ReportCreationRequest;
import gov.uk.ets.reports.api.web.model.ReportCreationResponse;
import gov.uk.ets.reports.api.web.model.ReportDto;
import gov.uk.ets.reports.model.ReportRequestingRole;
import gov.uk.ets.reports.model.ReportStatus;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.criteria.AccountSearchCriteria;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

// excludes our custom filters (like CustomLoggingFilter etc.)
@WebMvcTest(controllers = ReportController.class, excludeFilters = @ComponentScan.Filter(WebFilter.class))
// disables all spring security (and probably other filters too) filters
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ReportControllerTest {
    private static final String SAMPLE_FILE_1 = "sample-report-1.xlsx";
    private static final Long TEST_REPORT_ID = 1212L;
    private static final String TEST_URID = "UK1234567890";
    private static final long TEST_REPORT_ID_1 = 1L;
    private static final String TEST_NAME = "test_name";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private ReportService service;

    @MockBean
    private AuthorizationService authorizationService;
    
    @MockBean
    private ReportTypesPerRoleService reportTypesPerRoleService;

    private final DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @BeforeEach
    public void setUp() {
        when(authorizationService.getCurrentUserUrid()).thenReturn(TEST_URID);
    }

    @Test
    public void shouldCreateReportRequest() throws Exception {

        AccountSearchCriteria dummyCriteriaDto = new AccountSearchCriteria();
        dummyCriteriaDto.setAccountHolderName(TEST_NAME);
        ReportCreationRequest reportCreationRequest =
            ReportCreationRequest.builder()
                .type(ReportType.R0001)
                .requestingRole(ReportRequestingRole.administrator)
                .criteria(dummyCriteriaDto)
                .build();
        when(service.requestReport(reportCreationRequest, TEST_URID))
            .thenReturn(ReportCreationResponse.builder().reportId(TEST_REPORT_ID).build());
        when(authorizationService.checkUserAccessToReports(ReportRequestingRole.administrator, ReportType.R0001))
            .thenReturn(true);

        this.mockMvc.perform(
            post("/api-reports/reports.request")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reportCreationRequest))
        )
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(TEST_REPORT_ID.toString())));
    }

    @Test
    public void shouldThrowWhenInvalidCreationRequest() throws Exception {
        ReportCreationRequest reportCreationRequest =
            ReportCreationRequest.builder()
                .build();

        MvcResult result = this.mockMvc.perform(
            post("/api-reports/reports.request")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reportCreationRequest))
        )
            .andExpect(status().isBadRequest())
            .andExpect(
                r -> assertThat(r.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class)
            )
            .andReturn();

        ErrorBody error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorBody.class);

        assertThat(error).isNotNull();
        assertThat(error.getErrorDetails()).hasSize(1);
        assertThat(error.getErrorDetails().get(0).getMessage()).contains("type must not be null");
    }
    
    @Test
    public void shouldListAllReports() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ReportDto reportDto = ReportDto.builder()
            .id(TEST_REPORT_ID_1)
            .type(ReportType.R0001)
            .requestDate(now)
            .status(ReportStatus.PENDING)
            .build();
        when(service.getReports(TEST_URID, ReportRequestingRole.administrator)).thenReturn(List.of(reportDto));
        when(authorizationService.checkUserAccessToReports(ReportRequestingRole.administrator, null))
            .thenReturn(true);

        this.mockMvc.perform(
            get("/api-reports/reports.list")
            .param("urid", TEST_URID)
            .param("role", ReportRequestingRole.administrator.toString())
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id", is(1)))
            .andExpect(jsonPath("$.[0].type", is(ReportType.R0001.toString())))
            .andExpect(jsonPath("$.[0].requestDate", containsString(formatter.format(now))))
            .andExpect(jsonPath("$.[0].status", is("PENDING")))
        ;
    }

    @Test
    public void shouldDownloadReport() throws Exception {
        byte[] originalFileBytes = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(SAMPLE_FILE_1).toURI()));
        ReportDto reportDto = ReportDto.builder()
            .fileName(SAMPLE_FILE_1)
            .data(originalFileBytes)
            .build();
        when(service.downloadReport(TEST_REPORT_ID_1)).thenReturn(reportDto);

        this.mockMvc.perform(
            get("/api-reports/reports.download")
                .param("reportId", Long.toString(TEST_REPORT_ID_1))
        )
            .andExpect(status().isOk())
            .andExpect(
                header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"sample-report-1.xlsx\""))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM.toString()))
            .andExpect(content().bytes(originalFileBytes))
            .andReturn();
    }

    @Test
    public void shouldThrowWhenWrongReportType() throws Exception {
        String content =
            "{\"type\": \"WRONG\", \"criteria\": {\"accountHolderName\": \"test\"}}";

        MvcResult result = this.mockMvc.perform(
            post("/api-reports/reports.request")
                .contentType("application/json")
                .content(content)
        )
            .andExpect(status().isBadRequest())
            .andExpect(
                r -> assertThat(r.getResolvedException()).isInstanceOf(HttpMessageNotReadableException.class)
            )
            .andReturn();

        ErrorBody error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorBody.class);

        assertThat(error).isNotNull();
        assertThat(error.getErrorDetails()).hasSize(1);
        assertThat(error.getErrorDetails().get(0).getMessage()).contains(
            "Could not resolve type id 'WRONG' as a subtype of `gov.uk.ets.reports.model.criteria.ReportCriteria`: known type ids"
        );
    }

    @Test
    public void shouldThrowWhenWrongCriteriaType() throws Exception {
        String content =
            "{ \"type\": \"R0001\", \"criteria\": {\"accountHolderNameWrong\": \"test\"}}";

        MvcResult result = this.mockMvc.perform(
            post("/api-reports/reports.request")
                .contentType("application/json")
                .content(content)
        )
            .andExpect(status().isBadRequest())
            .andExpect(
                r -> assertThat(r.getResolvedException()).isInstanceOf(HttpMessageNotReadableException.class)
            )
            .andReturn();

        ErrorBody error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorBody.class);

        assertThat(error).isNotNull();
        assertThat(error.getErrorDetails()).hasSize(1);
        assertThat(error.getErrorDetails().get(0).getMessage()).contains(
            "Unrecognized field \"accountHolderNameWrong\" (class gov.uk.ets.reports.model.criteria.AccountSearchCriteria), not marked as ignorable");
    }
    
    @Test
    public void shouldThrowWhenWrongRequestingRoleType() throws Exception {
        String content =
            "{ \"type\": \"R0001\", \"requestingRole\": \"not_existing_role\", \"criteria\": {\"accountHolderNameWrong\": \"test\"}}";

        MvcResult result = this.mockMvc.perform(
            post("/api-reports/reports.request")
                .contentType("application/json")
                .content(content)
        )
            .andExpect(status().isBadRequest())
            .andExpect(
                r -> assertThat(r.getResolvedException()).isInstanceOf(HttpMessageNotReadableException.class)
            )
            .andReturn();

        ErrorBody error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorBody.class);

        assertThat(error).isNotNull();
        assertThat(error.getErrorDetails()).hasSize(1);
        assertThat(error.getErrorDetails().get(0).getMessage()).contains(
                "Cannot deserialize value of type `gov.uk.ets.reports.model.ReportRequestingRole` from String \"not_existing_role\": not one of the values accepted for Enum class: [authority, administrator]"
        );
    }
    
    @Test
    public void shouldListReportTypesForAdmins() throws Exception {
        List<ReportType> types = new ArrayList();
        types.add(ReportType.R0001);
        types.add(ReportType.R0025);
        when(reportTypesPerRoleService.getReportTypes(ReportRequestingRole.administrator)).thenReturn(types);
        when(authorizationService.checkUserAccessToReports(ReportRequestingRole.administrator, null))
            .thenReturn(true);

        this.mockMvc.perform(
            get("/api-reports/reports.list.eligible-types")
            .param("role", ReportRequestingRole.administrator.toString())
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0]", is(ReportType.R0001.toString())))
            .andExpect(jsonPath("$.[1]", is(ReportType.R0025.toString())))
        ;
    }
    
    @Test
    public void shouldAddReportsUserRole() throws Exception {
        this.mockMvc.perform(
            post("/api-reports/roles.add")
                .contentType("application/json")
                .headers(getHeaders())
                .content(objectMapper.writeValueAsString(getRequest()))
        )
            .andExpect(status().isOk());
        verify(authorizationService, times(1)).addUserRole(anyString(), anyString());
    }
    
    @Test
    public void shouldRemoveReportsUserRole() throws Exception {
        this.mockMvc.perform(
            post("/api-reports/roles.remove")
                .contentType("application/json")
                .headers(getHeaders())
                .content(objectMapper.writeValueAsString(getRequest()))
        )
            .andExpect(status().isOk());
        verify(authorizationService, times(1)).removeUserRole(anyString(), anyString());
    }
    
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.AUTHORIZATION, "bearer aaa-bbb-ccc");
        return headers;
    }

    private ReportAddRemoveRoleRequest getRequest() {
        ReportAddRemoveRoleRequest request =
                ReportAddRemoveRoleRequest.builder()
                .userId(TEST_URID)
                .requestingRole(ReportRequestingRole.administrator)
                .build();
        return request;
    }
}
