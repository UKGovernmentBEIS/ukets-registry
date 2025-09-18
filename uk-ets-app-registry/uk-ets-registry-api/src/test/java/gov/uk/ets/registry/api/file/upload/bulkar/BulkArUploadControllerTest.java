package gov.uk.ets.registry.api.file.upload.bulkar;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.commons.logging.Config;
import gov.uk.ets.registry.api.authz.AuthorizationServiceImpl;
import gov.uk.ets.registry.api.file.upload.dto.BaseType;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import gov.uk.ets.registry.api.file.upload.services.FileUploadProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;

@WebMvcTest(controllers = BulkArUploadController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class BulkArUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorizationServiceImpl authorizationService;

    @MockBean
    private FileUploadProcessor bulkArProcessor;

    @MockBean
    private BuildProperties buildProperties;

    @MockBean
    private Config config;

    private MockMultipartFile file;

    private FileHeaderDto dto;

    @BeforeEach
    void setUp() {
        file = new MockMultipartFile(
            "file",
            "hello.xlsx",
            MediaType.MULTIPART_FORM_DATA_VALUE,
            "Hello, World!".getBytes()
        );
        dto = new FileHeaderDto(1L, "hello.xlsx", BaseType.BULK_AR, ZonedDateTime.now());


    }

    @Test
    void upload_whenValid_return200() throws Exception {
        String resource = "/api-registry/bulk-ar.upload";
        Mockito.when(bulkArProcessor.loadAndVerifyFileIntegrity(file)).thenReturn(dto);
        mockMvc.perform(multipart(resource).file(file))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.fileName",is(dto.getFileName())))
               .andExpect(jsonPath("$.id",is(1)));
    }

    @Test
    void submit_whenValid_return200() throws Exception {
        String resource = "/api-registry/bulk-ar.submit";
        Mockito.when(bulkArProcessor.submitUploadedFile(any())).thenReturn(dto.getId());
        mockMvc.perform(post(resource).accept(MediaType.APPLICATION_JSON)
                                      .contentType(MediaType.APPLICATION_JSON)
                                      .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$",is(1)));
    }
}
