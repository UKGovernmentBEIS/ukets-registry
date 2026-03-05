package gov.uk.ets.registry.api.regulatornotice;

import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.common.search.SearchResponse;
import gov.uk.ets.registry.api.regulatornotice.service.RegulatorNoticeService;
import gov.uk.ets.registry.api.regulatornotice.shared.RegulatorNoticeProjection;
import gov.uk.ets.registry.api.regulatornotice.web.model.RegulatorNoticeSearchCriteria;
import gov.uk.ets.registry.api.regulatornotice.web.model.RegulatorNoticeSearchPageableMapper;
import gov.uk.ets.registry.api.regulatornotice.web.model.RegulatorNoticeSearchResult;
import gov.uk.ets.registry.api.task.shared.TaskSearchCriteria;
import gov.uk.ets.registry.api.task.web.model.TaskSearchResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The REST Controller for regulator notices.
 */
@Tag(name = "Regulator Notices")
@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RequiredArgsConstructor
public class RegulatorNoticeController {

    private final RegulatorNoticeService regulatorNoticeService;
    private final RegulatorNoticeSearchPageableMapper pageableMapper;

    /**
     * Searches for regulator notices according to the passed criteria.
     *
     * @param criteria       The {@link TaskSearchCriteria} search criteria
     * @param pageParameters The {@link PageParameters} parameters
     * @return The {@link SearchResponse} for {@link TaskSearchResult} response.
     */
    @GetMapping(path = "/regulator-notices.list", produces = MediaType.APPLICATION_JSON_VALUE)
    public SearchResponse<RegulatorNoticeSearchResult> search(RegulatorNoticeSearchCriteria criteria, PageParameters pageParameters,
                                                              @RequestHeader(name = "Is-Report", required = false)
                                                              boolean isReport) {
        Pageable pageable = pageableMapper.get(pageParameters);
        Page<RegulatorNoticeProjection> searchResults = regulatorNoticeService.search(criteria, pageable, isReport);
        SearchResponse<RegulatorNoticeSearchResult> response = new SearchResponse<>();
        pageParameters.setTotalResults(searchResults.getTotalElements());
        response.setPageParameters(pageParameters);
        response.setItems(searchResults.getContent().stream().map(RegulatorNoticeSearchResult::of).collect(
                Collectors.toList()));
        return response;
    }

    /**
     * @return All available regulator notice types.
     */
    @GetMapping(path = "regulator-notices.types", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getRegulatorNoticeTypes() {
        return ResponseEntity.ok(regulatorNoticeService.getRegulatorNoticeTypes());
    }
}
