package gov.uk.ets.registry.api.lock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RegistryTaskServiceTest {

    @Mock
    private RegistryTaskLockRepository lockRepository;
    @Mock
    private RegistryTaskLogRepository logRepository;

    private RegistryTaskService service;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        service = new RegistryTaskService(lockRepository, logRepository);
    }

    @Test
    void testSameAsLatest() {
        // given
        RegistryTaskLog log = new RegistryTaskLog();
        log.setId("same");
        when(logRepository.findFirstByOrderByExecutedDateDesc()).thenReturn(Optional.of(log));

        // when
        boolean result = service.sameAsLatest("same");

        // then
        assertTrue(result);
    }

    @Test
    void testNotSameAsLatest() {
        // given
        when(logRepository.findFirstByOrderByExecutedDateDesc()).thenReturn(Optional.empty());

        // when
        boolean result = service.sameAsLatest("same");

        // then
        assertFalse(result);
    }

    @Test
    void testAcquireLock() {
        // given
        RegistryTaskLock lock = new RegistryTaskLock();
        when(lockRepository.findByAcquiredBy(anyString())).thenReturn(Optional.of(lock));

        // when
        boolean result = service.acquireLock();

        // then
        verify(lockRepository, times(1)).acquireLock(anyString(), eq(60));
        assertTrue(result);
    }

    @Test
    void testNotAcquireLock() {
        // given
        when(lockRepository.findByAcquiredBy(anyString())).thenReturn(Optional.empty());

        // when
        boolean result = service.acquireLock();

        // then
        verify(lockRepository, times(1)).acquireLock(anyString(), eq(60));
        assertFalse(result);
    }

    @Test
    void testRelease() {
        // given
        RegistryTaskLock lock = new RegistryTaskLock();
        when(lockRepository.findByAcquiredBy(anyString())).thenReturn(Optional.of(lock));

        // when
        service.release("identifier");

        // then
        verify(lockRepository, times(1)).save(lock);
        verify(logRepository, times(1)).save(any(RegistryTaskLog.class));
    }

    @Test
    void testReleaseFailed() {
        // given
        when(lockRepository.findByAcquiredBy(anyString())).thenReturn(Optional.empty());

        // when
        assertThrows(RuntimeException.class, () -> service.release("identifier"));
    }
}
