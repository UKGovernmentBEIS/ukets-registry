package gov.uk.ets.registry.api.itl.reconciliation;

import gov.uk.ets.registry.api.itl.reconciliation.service.ITLReconciliationAdminService;
import gov.uk.ets.registry.api.itl.reconciliation.web.model.ITLReconciliationDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
public class ITLReconciliationController {

    private final ITLReconciliationAdminService reconciliationService;

    @GetMapping("itl-reconciliation.get.latest")
    public ITLReconciliationDTO getLatestReconciliation() {
        return reconciliationService.getLatestReconciliation();
    }
}
