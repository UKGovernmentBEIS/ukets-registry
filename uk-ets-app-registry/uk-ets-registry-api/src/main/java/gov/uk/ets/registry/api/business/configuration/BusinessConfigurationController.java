package gov.uk.ets.registry.api.business.configuration;

import gov.uk.ets.registry.api.business.configuration.service.BusinessConfigurationService;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Map;

/**
 * Handles requests related to Business Configuration.
 */
@Tag(name = "Business Configuration")
@RestController
@RequestMapping(path = "/api-registry/configuration", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class BusinessConfigurationController {

    private BusinessConfigurationService businessConfigurationService;

    public BusinessConfigurationController(BusinessConfigurationService businessConfigurationService) {
        this.businessConfigurationService = businessConfigurationService;
    }

    /**
     * Retrieves either the requested application property if the key is provided
     * or all the available application properties
     *
     * @param key is optional
     * @return the requested Map of application properties
     * @throws IOException
     */
    @GetMapping
    @ResponseBody
    public ResponseEntity<Map<String, String>> getApplicationProperties(@RequestParam(required = false) String key)
            throws IOException {

        final Map<String, String> dto = (key == null) ? businessConfigurationService.getApplicationProperties() :
                businessConfigurationService.getApplicationPropertyByKey(key);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}
