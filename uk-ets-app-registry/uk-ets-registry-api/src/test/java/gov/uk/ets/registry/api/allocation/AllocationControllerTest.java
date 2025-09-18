package gov.uk.ets.registry.api.allocation;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.allocation.domain.AllocationJob;
import gov.uk.ets.registry.api.allocation.service.AccountAllocationService;
import gov.uk.ets.registry.api.allocation.service.AllocationJobService;
import gov.uk.ets.registry.api.allocation.service.AllocationReportGenerator;
import gov.uk.ets.registry.api.allocation.service.AllocationYearCapService;
import gov.uk.ets.registry.api.allocation.service.RequestAllocationService;
import gov.uk.ets.registry.api.allocation.service.TestUtils;
import gov.uk.ets.registry.api.allocation.service.dto.AccountAllocationDTO;
import gov.uk.ets.registry.api.allocation.service.dto.AggregatedAllocationDTO;
import gov.uk.ets.registry.api.allocation.service.dto.AllocationJobSearchCriteria;
import gov.uk.ets.registry.api.allocation.service.dto.AnnualAllocationDTO;
import gov.uk.ets.registry.api.allocation.service.dto.TotalAllocationDTO;
import gov.uk.ets.registry.api.allocation.service.dto.UpdateAllocationCommand;
import gov.uk.ets.registry.api.allocation.type.AllocationJobStatus;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.allocation.web.model.UpdateAllocationStatusRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AllocationControllerTest {
    private Long accountId;
    @Mock
    private AccountAllocationService accountAllocationService;
    @Mock
    private RequestAllocationService requestAllocationService;
    @Mock
    private AllocationYearCapService allocationYearCapService;

    @Mock
    private AllocationJobService allocationJobService;
    @Mock
    private AllocationReportGenerator reportGenerator;
    private MockMvc mockMvc;
    private AllocationController controller;

    @BeforeEach
    void setUp() {
        accountId = 12323L;
        controller = new AllocationController(
            accountAllocationService, requestAllocationService,allocationYearCapService, allocationJobService, reportGenerator
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAccountAllocation() throws Exception {
        String url = "/api-registry/allocations.get";
        mockMvc.perform(get(url)
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
        AccountAllocationDTO expectedDto = AccountAllocationDTO.builder()
            .standard(AggregatedAllocationDTO.builder()
                .annuals(List.of(AnnualAllocationDTO.builder()
                    .year(2021)
                    .status(AllocationStatusType.WITHHELD.name())
                    .allocated(20L)
                    .entitlement(80L)
                    .remaining(60L)
                    .build()))
                .totals(TotalAllocationDTO.builder()
                    .entitlement(80L)
                    .allocated(20L)
                    .remaining(60L)
                    .build())
                .build())
            .underNewEntrantsReserve(AggregatedAllocationDTO.builder()
                .annuals(List.of(AnnualAllocationDTO.builder()
                    .year(2023)
                    .status(AllocationStatusType.WITHHELD.name())
                    .allocated(200L)
                    .entitlement(800L)
                    .remaining(600L)
                    .build()))
                .totals(TotalAllocationDTO.builder()
                    .entitlement(800L)
                    .allocated(200L)
                    .remaining(600L)
                    .build())
                .build())
            .build();
        given(accountAllocationService.getAccountAllocation(accountId)).willReturn(expectedDto);

        //when
        mockMvc.perform(get(url)
            .contentType(MediaType.APPLICATION_JSON)
            .param("accountId", accountId.toString())
        ).andExpect(status().isOk())
            .andExpect(jsonPath("$.standard", notNullValue()))
            .andExpect(jsonPath("$.standard.annuals[0]", notNullValue()))
            .andExpect(jsonPath("$.standard.annuals[0].year", is(expectedDto.getStandard().getAnnuals().get(0).getYear())))
            .andExpect(jsonPath("$.standard.annuals[0].entitlement", is(expectedDto.getStandard().getAnnuals().get(0).getEntitlement().intValue())))
            .andExpect(jsonPath("$.standard.annuals[0].allocated", is(expectedDto.getStandard().getAnnuals().get(0).getAllocated().intValue())))
            .andExpect(jsonPath("$.standard.annuals[0].remaining", is(expectedDto.getStandard().getAnnuals().get(0).getRemaining().intValue())))
            .andExpect(jsonPath("$.standard.annuals[0].status", is(expectedDto.getStandard().getAnnuals().get(0).getStatus())))
            .andExpect(jsonPath("$.standard.totals", notNullValue()))
            .andExpect(jsonPath("$.standard.totals.entitlement", is(expectedDto.getStandard().getTotals().getEntitlement().intValue())))
            .andExpect(jsonPath("$.standard.totals.allocated", is(expectedDto.getStandard().getTotals().getAllocated().intValue())))
            .andExpect(jsonPath("$.standard.totals.remaining", is(expectedDto.getStandard().getTotals().getRemaining().intValue())))
            .andExpect(jsonPath("$.underNewEntrantsReserve", notNullValue()))
            .andExpect(jsonPath("$.underNewEntrantsReserve.annuals[0]", notNullValue()))
            .andExpect(jsonPath("$.underNewEntrantsReserve.annuals[0].year", is(expectedDto.getUnderNewEntrantsReserve().getAnnuals().get(0).getYear())))
            .andExpect(jsonPath("$.underNewEntrantsReserve.annuals[0].entitlement", is(expectedDto.getUnderNewEntrantsReserve().getAnnuals().get(0).getEntitlement().intValue())))
            .andExpect(jsonPath("$.underNewEntrantsReserve.annuals[0].allocated", is(expectedDto.getUnderNewEntrantsReserve().getAnnuals().get(0).getAllocated().intValue())))
            .andExpect(jsonPath("$.underNewEntrantsReserve.annuals[0].remaining", is(expectedDto.getUnderNewEntrantsReserve().getAnnuals().get(0).getRemaining().intValue())))
            .andExpect(jsonPath("$.underNewEntrantsReserve.annuals[0].status", is(expectedDto.getUnderNewEntrantsReserve().getAnnuals().get(0).getStatus())))
            .andExpect(jsonPath("$.underNewEntrantsReserve.totals", notNullValue()))
            .andExpect(jsonPath("$.underNewEntrantsReserve.totals.entitlement", is(expectedDto.getUnderNewEntrantsReserve().getTotals().getEntitlement().intValue())))
            .andExpect(jsonPath("$.underNewEntrantsReserve.totals.allocated", is(expectedDto.getUnderNewEntrantsReserve().getTotals().getAllocated().intValue())))
            .andExpect(jsonPath("$.underNewEntrantsReserve.totals.remaining", is(expectedDto.getUnderNewEntrantsReserve().getTotals().getRemaining().intValue())));

        //then
        then(accountAllocationService).should(times(1)).getAccountAllocation(accountId);
    }

    @Test
    void updateAccountAllocationStatusShouldFailWith400BadRequest() throws Exception {
        String api = "/api-registry/allocations.update.status";
        Map<Integer, AllocationStatusType> changedStatus = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        // given
        UpdateAllocationStatusRequest request = new UpdateAllocationStatusRequest();
        request.setJustification("test test");
        request.setChangedStatus(changedStatus);
        // when then
        mockMvc.perform(
            post(api).accept(MediaType.APPLICATION_JSON)
                .param("accountId", accountId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest());

        request.setJustification(null);
        changedStatus.put(2021, AllocationStatusType.ALLOWED);
        mockMvc.perform(
            post(api).accept(MediaType.APPLICATION_JSON)
                .param("accountId", accountId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest());

        request.setJustification("test test test");
        mockMvc.perform(
            post(api).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest());

        mockMvc.perform(
            post(api).accept(MediaType.APPLICATION_JSON)
                .param("accountId", accountId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        ).andExpect(status().isOk());
    }

    @Test
    void updateAccountAllocation() throws Exception {
        // given
        ArgumentCaptor<UpdateAllocationCommand> updateCommand = ArgumentCaptor.forClass(UpdateAllocationCommand.class);
        ObjectMapper mapper = new ObjectMapper();
        String api = "/api-registry/allocations.update.status";
        UpdateAllocationStatusRequest request = new UpdateAllocationStatusRequest();
        request.setChangedStatus(new HashMap<>());
        request.getChangedStatus().put(2022, AllocationStatusType.ALLOWED);
        request.getChangedStatus().put(2023, AllocationStatusType.WITHHELD);
        request.setJustification("test test test test");

        // when
        MvcResult result = mockMvc.perform(
            post(api).accept(MediaType.APPLICATION_JSON)
                .param("accountId", accountId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        ).andReturn();

        // then
        then(accountAllocationService).should(times(1)).updateAllocationStatus(updateCommand.capture());
        UpdateAllocationCommand command = updateCommand.getValue();
        assertEquals(command.getAccountId(), accountId);
        assertEquals(command.getChangedStatus(), request.getChangedStatus());
        assertEquals(command.getJustification(), request.getJustification());

        assertEquals(accountId.toString(), result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("If allocation job is found return isPending: true")
    void getIsAllocationJobPending() throws Exception {
        var url = "/api-registry/allocations.get.pending";
        var scheduledJob = TestUtils.createAllocationJob(AllocationJobStatus.SCHEDULED);
        given(allocationJobService.getScheduledJobs()).willReturn(List.of(scheduledJob));
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isPending", notNullValue()))
            .andExpect(jsonPath("$.isPending", is(true)));
        then(allocationJobService).should(times(1)).getScheduledJobs();
    }

    @Test
    @DisplayName("If allocation job not return isPending: false")
    void getIsAllocationJobNotPending() throws Exception {
        var url = "/api-registry/allocations.get.pending";
        given(allocationJobService.getScheduledJobs()).willReturn(Collections.emptyList());
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isPending", notNullValue()))
            .andExpect(jsonPath("$.isPending", is(false)));
        then(allocationJobService).should(times(1)).getScheduledJobs();
    }

    @Test
    @DisplayName("When cancel job has not thrown an exception, canceling it is successful")
    void whenIsPendingAllocationCancelIsSuccess() throws Exception {
        var url = "/api-registry/allocations.cancel.pending/1";
        mockMvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
        then(allocationJobService).should(times(1)).cancelPendingJob(1L);
    }

    @Test
    @DisplayName("If allocation pending tasks exist return true")
    void getAccountAllocationPendingTaskExists() throws Exception {
        var url = "/api-registry/allocations.get.pendingTaskExists";
        given(accountAllocationService.getAccountAllocationPendingTaskExists(accountId)).willReturn(Boolean.TRUE);
        
        MvcResult result = mockMvc.perform(get(url)
            .param("accountId", accountId.toString()))
            .andExpect(status().isOk())
            .andReturn();
        assertEquals(Boolean.TRUE.toString(), result.getResponse().getContentAsString());
        then(accountAllocationService).should(times(1)).getAccountAllocationPendingTaskExists(accountId);
    }

    @Test
    @DisplayName("Allocation Jobs search")
    void searchAllocationJobs() throws Exception {
        var url = "/api-registry/allocations.list";

        AllocationJob job = new AllocationJob();
        given(allocationJobService.searchAllocationJobs(any(AllocationJobSearchCriteria.class), any(Pageable.class)))
            .willReturn(new PageImpl<>(List.of(job)));

        MvcResult result = mockMvc.perform(get(url)
            .queryParam("sortDirection", "DESC"))
            .andExpect(status().isOk())
            .andReturn();
        assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Allocation Job cancel")
    void cancelAllocationJob() throws Exception {
        var url = "/api-registry/allocations.cancel.pending/111";

        mockMvc.perform(delete(url))
            .andExpect(status().isNoContent())
            .andReturn();

        verify(allocationJobService, times(1)).cancelPendingJob(111L);
    }
}
