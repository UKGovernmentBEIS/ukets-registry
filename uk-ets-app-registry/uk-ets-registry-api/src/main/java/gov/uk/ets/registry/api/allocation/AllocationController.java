package gov.uk.ets.registry.api.allocation;

import static gov.uk.ets.commons.logging.RequestParamType.ACCOUNT_ID;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.allocation.data.AllowanceReport;
import gov.uk.ets.registry.api.allocation.domain.AllocationJob;
import gov.uk.ets.registry.api.allocation.service.AccountAllocationService;
import gov.uk.ets.registry.api.allocation.service.AllocationJobService;
import gov.uk.ets.registry.api.allocation.service.AllocationReportGenerator;
import gov.uk.ets.registry.api.allocation.service.AllocationYearCapService;
import gov.uk.ets.registry.api.allocation.service.RequestAllocationService;
import gov.uk.ets.registry.api.allocation.service.dto.AccountAllocationDTO;
import gov.uk.ets.registry.api.allocation.service.dto.AllocationJobSearchCriteria;
import gov.uk.ets.registry.api.allocation.service.dto.AllocationJobSearchResult;
import gov.uk.ets.registry.api.allocation.service.dto.AllocationJobSortFieldParam;
import gov.uk.ets.registry.api.allocation.service.dto.UpdateAllocationCommand;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.allocation.web.model.AllocationRequest;
import gov.uk.ets.registry.api.allocation.web.model.UpdateAllocationStatusRequest;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.authz.ruleengine.features.AdminsWithAccountAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AuthoritiesWithAccountAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.CannotSubmitRequestWhenAccountIsTransferPendingStatusRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.UserStatusEnrolledRule;
import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.common.search.PageableMapper;
import gov.uk.ets.registry.api.common.search.SearchResponse;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@AllArgsConstructor
public class AllocationController {

    private static final DateTimeFormatter fileNameDateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private final AccountAllocationService service;
    private final RequestAllocationService requestAllocationService;
    private final AllocationYearCapService allocationYearCapService;
    private final AllocationJobService allocationJobService;
    private final AllocationReportGenerator allocationReportGenerator;

    @GetMapping("allocations.get")
    public AccountAllocationDTO getAccountAllocation(@RequestParam @MDCParam(ACCOUNT_ID) Long accountId) {
        return service.getAccountAllocation(accountId);
    }

    @GetMapping("allocations.get.status")
    public Map<Integer, AllocationStatusType> getAccountAllocationStatus(@RequestParam @MDCParam(ACCOUNT_ID) Long accountId) {
        return service.getAccountAllocationStatus(accountId);
    }
    
    @GetMapping("allocations.get.pendingTaskExists")
    public ResponseEntity<Boolean> getAccountAllocationPendingTaskExists(@RequestParam @MDCParam(ACCOUNT_ID) Long accountId) {
        return ResponseEntity.ok(service.getAccountAllocationPendingTaskExists(accountId));
    }

    @GetMapping("allocations.get.issuance-by-year")
    public List<AllowanceReport> getAllocationYearsForCurrentPhaseWithTotals() {
        return allocationYearCapService.getAllocationYearsForCurrentPhaseWithTotals();
    }

    @GetMapping("allocations.list")
    public SearchResponse<AllocationJobSearchResult> search(AllocationJobSearchCriteria criteria,
                                                            PageParameters pageParameters) {

        SearchResponse<AllocationJobSearchResult> response = new SearchResponse<>();
        PageableMapper pageableMapper =
            new PageableMapper(AllocationJobSortFieldParam.values(), AllocationJobSortFieldParam.ALLOCATION_JOB_ID);
        Pageable pageable = pageableMapper.get(pageParameters);

        Page<AllocationJob> page = allocationJobService.searchAllocationJobs(criteria, pageable);

        response.setItems(page.getContent().stream().map(AllocationJobSearchResult::from).toList());
        pageParameters.setTotalResults(page.getTotalElements());
        response.setPageParameters(pageParameters);

        return response;
    }

    @GetMapping("allocations.get.pending")
    public ResponseEntity<Map<String, Boolean>> isAllocationPending() {
        var isPending = !allocationJobService.getScheduledJobs().isEmpty();
        return ResponseEntity.ok(Collections.singletonMap("isPending", isPending));
    }

    @DeleteMapping("allocations.cancel.pending/{allocationJobId}")
    public ResponseEntity<?> cancelPendingAllocations(@PathVariable Long allocationJobId) {
        allocationJobService.cancelPendingJob(allocationJobId);
        return ResponseEntity.noContent().build();
    }

    @Protected({
        CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
        SeniorAdminRule.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    @PostMapping(path = "allocations.update.status", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String updateAccountAllocationStatus(@RequestBody @Valid UpdateAllocationStatusRequest request,
                                                @RuleInput(RuleInputType.ACCOUNT_ID)
                                                @RequestParam @MDCParam(ACCOUNT_ID) Long accountId) {
        service.updateAllocationStatus(UpdateAllocationCommand.builder()
            .accountId(accountId)
            .changedStatus(request.getChangedStatus())
            .justification(request.getJustification())
            .build());
        return accountId.toString();
    }

    @GetMapping("allocations.get.available-years")
    public List<Integer> getAvailableAllocationYears() {
        return requestAllocationService.getAvailableAllocationYears();
    }

    @Protected({
        UserStatusEnrolledRule.class
    })
    @PostMapping(path = "allocations.submit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public BusinessCheckResult submitAllocationRequest(@RequestBody @Valid AllocationRequest allocationRequest) {
        return requestAllocationService.submit(allocationRequest.getAllocationYear(), allocationRequest.getAllocationCategory());
    }

    /**
     * Retrieves a file with allocations
     *
     * @param allocationYear The allocation year
     * @return The requested file
     */
    @GetMapping(path = "/allocations.get.file", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getAllocationsFile(@RequestParam Integer allocationYear,
                                                     @RequestParam AllocationCategory allocationCategory) {
        String filename = "UK_AllocationJob_" + allocationYear + "_" + allocationCategory + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION);
        headers.add(CONTENT_DISPOSITION,
            ContentDisposition.builder("attachment").filename(filename)
                .build().toString());
        return new ResponseEntity<>(requestAllocationService.getAllocationsFile(allocationYear, allocationCategory), headers,
            HttpStatus.OK);
    }

    /**
     * Retrieves the report file for the specified allocation job
     *
     * @param jobId The allocation job id
     * @return The requested file
     */
    @GetMapping(path = "/allocations.get.report.file", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getAllocationsReportFile(@RequestParam Long jobId) {

        AllocationJob allocationJob = allocationJobService.getJob(jobId);
        String filename = jobId + "_" + allocationJob.getCategory() + "_" + allocationJob.getYear() + "_" +
            fileNameDateFormatter.format(LocalDateTime.now()) + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION);
        headers.add(CONTENT_DISPOSITION,
            ContentDisposition.builder("attachment").filename(filename)
                .build().toString());
        return new ResponseEntity<>(allocationReportGenerator.generate(jobId), headers,
            HttpStatus.OK);
    }
}
