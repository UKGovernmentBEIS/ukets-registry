package gov.uk.ets.registry.api.common.web;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class UkEtsExceptionControllerAdviceTest {
    private MockMvc mockMvc;

    private DummyController dummyController;

    @BeforeEach
    void setUp() {
        dummyController = new DummyController();
        mockMvc = MockMvcBuilders.standaloneSetup(dummyController)
            .setControllerAdvice(new UkEtsExceptionControllerAdvice())
            .build();
        // could not find any other way to retrieve config properties here
        // and this is needed for the NoHandlerFound exception handler to kick in
        mockMvc.getDispatcherServlet().setThrowExceptionIfNoHandlerFound(true);

    }

    @Test
    public void shouldSuccessfullyCallValidPath() throws Exception {
        mockMvc.perform(get("/api-registry/valid"))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldGetABadRequestResponseWhenNoHandlerFound() throws Exception {
        mockMvc.perform(get("/api-registry/invalid"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetails[0].message", is("No handler found for GET /api-registry/invalid")));
    }

    @Test
    public void shouldForwardToIndexPageIfNotApiCall() throws Exception {
        mockMvc.perform(get("/dashboard"))
            .andExpect(status().isOk())
            .andExpect(forwardedUrl("/index.html"));
    }

    /**
     * Used only to check that the handler will not kick in in case of valid API call.
     */
    @RestController
    @RequestMapping("/api-registry")
    private class DummyController {

        @GetMapping(path = "/valid")
        public ResponseEntity<Object> valid() {
            return ResponseEntity.ok().build();
        }
    }
}


