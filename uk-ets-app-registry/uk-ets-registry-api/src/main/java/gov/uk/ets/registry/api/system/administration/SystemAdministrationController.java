package gov.uk.ets.registry.api.system.administration;

import gov.uk.ets.registry.api.system.administration.service.SystemAdministrationService;
import gov.uk.ets.registry.api.system.administration.web.model.ResetDatabaseActionResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles requests for system administration.
 */
@Tag(name = "System Administration")
@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(name = "system.administration.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class SystemAdministrationController {

    /**
     * Service for system administration.
     */
    private final SystemAdministrationService systemAdministrationService;

    @PostMapping(value = "/system-administration.reset", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResetDatabaseActionResult> reset() {
        ResetDatabaseActionResult result = systemAdministrationService.reset();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
