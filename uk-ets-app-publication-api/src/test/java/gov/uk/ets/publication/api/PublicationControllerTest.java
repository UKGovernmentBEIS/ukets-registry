package gov.uk.ets.publication.api;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.publication.api.authz.AuthorizationService;
import gov.uk.ets.publication.api.model.DisplayType;
import gov.uk.ets.publication.api.model.SectionType;
import gov.uk.ets.publication.api.service.SectionService;
import gov.uk.ets.publication.api.web.model.ReportFileDto;
import gov.uk.ets.publication.api.web.model.SectionDto;
import gov.uk.ets.reports.model.ReportType;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.servlet.annotation.WebFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// excludes our custom filters (like CustomLoggingFilter etc.)
@WebMvcTest(controllers = PublicationController.class, excludeFilters = @ComponentScan.Filter(WebFilter.class))
// disables all spring security (and probably other filters too) filters
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class PublicationControllerTest {

    private static final String ID = "123";
    private static final String TEST_URID = "UK1234567890";

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockitoBean
    private SectionService service;

    @MockitoBean
    private AuthorizationService authorizationService;

    @Test
    void shouldListAllEtsSections() throws Exception {
        when(service.getSections(SectionType.ETS)).thenReturn(List.of(createSectionDto()));
        when(service.getSections(SectionType.KP)).thenReturn(List.of(new SectionDto()));

        this.mockMvc.perform(
            get("/api-publication/sections.list")
            .param("sectionType", SectionType.ETS.toString())
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].title", is("title")))
            .andExpect(jsonPath("$.[0].summary", is("summary")))
            .andExpect(jsonPath("$.[0].displayOrder", is(1)))
            .andExpect(jsonPath("$.[0].reportType", is(ReportType.R0001.toString())))
            .andExpect(jsonPath("$.[0].displayType", is(DisplayType.ONE_FILE.toString())))
        ;
    }
    
    @Test
    void shouldGetSectionDetails() throws Exception {
        when(service.getSection(any())).thenReturn(createSectionDto());

        this.mockMvc.perform(
            get("/api-publication/sections.get")
            .param("id", ID)
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("title")))
            .andExpect(jsonPath("$.summary", is("summary")))
            .andExpect(jsonPath("$.displayOrder", is(1)))
            .andExpect(jsonPath("$.reportType", is(ReportType.R0001.toString())))
            .andExpect(jsonPath("$.displayType", is(DisplayType.ONE_FILE.toString())))
        ;
    }
    
    @Test
    void shouldUpdateSectionDetails() throws Exception {
        when(service.updateSection(any())).thenReturn(123L);

        this.mockMvc.perform(
            post("/api-publication/sections.update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createSectionDto()))
        )
            .andExpect(status().isOk())
        ;
    }
    
    @Test
    void shouldListAllFilesForSection() throws Exception {
        when(service.getFiles(any(), any())).thenReturn(List.of(createReportFileDto()));

        this.mockMvc.perform(
            get("/api-publication/sections.list-files")
            .param("id", ID)
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].fileName", is("filename")))
            .andExpect(jsonPath("$.[0].applicableForYear", is(2030)))

        ;
    }
    
    @Test
    void shouldUnpublishFile() throws Exception {
        when(service.unpublishFile(any())).thenReturn(123L);

        this.mockMvc.perform(
            post("/api-publication/sections.unpublish-file")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createReportFileDto()))
        )
            .andExpect(status().isOk())
        ;
    }
    
    @Test
    void shouldDownloadFile() throws Exception {
        when(service.downloadFile(any())).thenReturn(createReportFileDto());

        this.mockMvc.perform(
            get("/api-publication/sections.download-file")
            .param("id", ID)
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
        ;
    }

    @Test
    void shouldAddReportsUserRole() throws Exception {
        this.mockMvc.perform(
                        post("/api-publication/roles.add")
                                .contentType("application/json")
                                .headers(getHeaders())
                                .content(objectMapper.writeValueAsString(TEST_URID))
                )
                .andExpect(status().isOk());
        verify(authorizationService, times(1)).addUserRole(anyString(), anyString());
    }

    @Test
    void shouldRemoveReportsUserRole() throws Exception {
        this.mockMvc.perform(
                        post("/api-publication/roles.remove")
                                .contentType("application/json")
                                .headers(getHeaders())
                                .content(objectMapper.writeValueAsString(TEST_URID))
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

    private SectionDto createSectionDto() {
        LocalDateTime date = LocalDateTime.now();
        SectionDto sectionDto = new SectionDto();
        sectionDto.setId(123L);
        sectionDto.setTitle("title");
        sectionDto.setSummary("summary");
        sectionDto.setDisplayOrder(1);
        sectionDto.setReportType(ReportType.R0001);
        sectionDto.setDisplayType(DisplayType.ONE_FILE);
        sectionDto.setLastUpdated(date);
        return sectionDto;
    }

    private ReportFileDto createReportFileDto() {
        LocalDateTime date = LocalDateTime.now();
        ReportFileDto reportFileDto = new ReportFileDto();
        reportFileDto.setId(123L);
        reportFileDto.setFileName("filename");
        reportFileDto.setApplicableForYear(2030);
        reportFileDto.setPublishedOn(date);
        return reportFileDto;
    }

}
