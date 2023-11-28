package uk.gov.ets.transaction.log.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.ets.transaction.log.domain.AllocationPhase;
import uk.gov.ets.transaction.log.repository.AllocationPhaseRepository;

class LimitServiceTest {

    @InjectMocks
    LimitService limitService;

    @Mock
    AllocationPhaseRepository allocationPhaseRepository;

    @BeforeEach
    void setUp() {
        initMocks(this);
        limitService = new LimitService(allocationPhaseRepository);
    }

    @Test
    void testAllocationPhaseNotSet() {
        when(allocationPhaseRepository.getAllocationPhaseBasedOnYear(any())).thenReturn(Optional.empty());
        assertEquals(0L, limitService.getIssuanceLimit());
    }

    @Test
    void testLimitNotSet() {
        when(allocationPhaseRepository.getAllocationPhaseBasedOnYear(any())).thenReturn(Optional.of(new AllocationPhase() {{
            setInitialPhaseCap(0L);
            setConsumedPhaseCap(0L);
        }}));
        assertEquals(0L, limitService.getIssuanceLimit());
    }

    @Test
    void testLimitFullyConsumed() {
        when(allocationPhaseRepository.getAllocationPhaseBasedOnYear(any())).thenReturn(Optional.of(new AllocationPhase() {{
            setInitialPhaseCap(100_000L);
            setConsumedPhaseCap(100_000L);
        }}));
        assertEquals(0L, limitService.getIssuanceLimit());
    }

    @Test
    void testLimitAvailableLow() {
        when(allocationPhaseRepository.getAllocationPhaseBasedOnYear(any())).thenReturn(Optional.of(new AllocationPhase() {{
            setInitialPhaseCap(100_000L);
            setConsumedPhaseCap(99_950L);
        }}));
        assertEquals(50L, limitService.getIssuanceLimit());
    }

    @Test
    void testLimitAvailableHigh() {
        ReflectionTestUtils.setField(limitService, "allocationYear", 2022);
        when(allocationPhaseRepository.getAllocationPhaseBasedOnYear(any())).thenReturn(Optional.of(new AllocationPhase() {{
            setInitialPhaseCap(100_000L);
            setConsumedPhaseCap(1000L);
        }}));
        assertEquals(100_000L - 1000L, limitService.getIssuanceLimit());
    }

    @Test
    void consumePhaseNotSet() {
        when(allocationPhaseRepository.getAllocationPhaseBasedOnYear(any())).thenReturn(Optional.empty());
        limitService.consumeLimit(10L);
        verify(allocationPhaseRepository, times(0)).save(any());
    }

    @Test
    void consume() {
        when(allocationPhaseRepository.getAllocationPhaseBasedOnYear(any())).thenReturn(Optional.of(new AllocationPhase() {{
            setInitialPhaseCap(100_000L);
            setConsumedPhaseCap(1000L);
        }}));
        limitService.consumeLimit(10L);
        verify(allocationPhaseRepository, times(1)).save(any());
    }

}