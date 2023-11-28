package gov.uk.ets.registry.api.reconciliation;

import gov.uk.ets.registry.api.reconciliation.domain.Reconciliation;
import gov.uk.ets.registry.api.reconciliation.service.ProcessReconciliationService;
import gov.uk.ets.registry.api.reconciliation.service.ReconciliationService;
import gov.uk.ets.registry.api.reconciliation.web.DTOMapper;
import gov.uk.ets.registry.api.reconciliation.web.ReconciliationDTO;
import java.util.Date;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReconciliationController {
    private final ReconciliationService reconciliationService;
    private final ProcessReconciliationService processReconciliationService;
    private final DTOMapper mapper;

    /**
     * Gets the latest started reconciliation
     * @return The latest started reconciliation.
     */
    @GetMapping("reconciliation.get.latest")
    public ReconciliationDTO getLatestReconciliation() {
        Reconciliation latestReconciliation = reconciliationService.getLatestReconciliation();
        return mapper.map(latestReconciliation);
    }

    /**
     * Starts a new reconciliation
     */
    @PostMapping(value = "reconciliation.start", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ReconciliationDTO startReconciliation() {
        processReconciliationService.initiate(new Date());
        return getLatestReconciliation();
    }
}
