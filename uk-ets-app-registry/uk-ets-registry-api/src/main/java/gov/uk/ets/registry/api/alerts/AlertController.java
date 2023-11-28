package gov.uk.ets.registry.api.alerts;

import gov.uk.ets.registry.api.alerts.service.AlertsService;
import gov.uk.ets.registry.api.alerts.web.model.AlertsResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Log4j2
@AllArgsConstructor
public class AlertController {

    private AlertsService alertsService;

    @GetMapping("/alerts.get.details")
    public ResponseEntity<AlertsResponse> getAlertDetails(@RequestParam("urId") String urId) {
        return new ResponseEntity<>(alertsService.getAlertsDetails(urId), HttpStatus.OK);
    }
}
