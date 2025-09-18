package gov.uk.ets.registry.api.lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

import gov.uk.ets.registry.api.common.test.RegistryIntegrationTest;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

@RegistryIntegrationTest
class RegistryLockFacadeIntegrationTest {

    @Value("classpath:keycloak/uk-ets-registry-api-authz-config.json")
    private Resource resource;
    @Autowired
    private RegistryLockFacade lockFacade;
    @SpyBean
    private RegistryTaskLockRepository lockRepository;
    @Autowired
    private RegistryTaskLogRepository logRepository;

    private boolean updated;

    @BeforeEach
    public void setup() {
        updated = false;
        RegistryTaskLock lock = lockRepository.findAll().get(0);
        lock.setActive(false);
        lockRepository.save(lock);
        logRepository.deleteAll();
    }

    @Test
    void testExecution() throws Exception {
        // given
        Consumer<Resource> consumer = getConsumer();

        // when
        lockFacade.execute(resource, consumer);

        // then
        assertEquals(1, logRepository.findAll().size());
        assertFalse(lockRepository.findAll().get(0).isActive());
        assertTrue(updated);
    }

    @Test
    void testIgnoreExecution() throws Exception {
        // given
        insertCurrentResourceIdentifier();

        Consumer<Resource> consumer = getConsumer();

        // when
        lockFacade.execute(resource, consumer);

        // then
        assertEquals(1, logRepository.findAll().size());
        assertFalse(lockRepository.findAll().get(0).isActive());
        assertFalse(updated);
    }

    @Test
    void testAwaitExecution() throws Exception {
        // given
        RegistryTaskLock existingLock = lockRepository.findAll().get(0);
        existingLock.setActive(true);
        existingLock.setAcquiredBy("me");
        existingLock.setAcquiredAt(Date.from(Instant.now().minusMillis(59500L)));
        lockRepository.save(existingLock);

        Consumer<Resource> consumer = getConsumer();

        // when
        lockFacade.execute(resource, consumer);

        // then
        assertEquals(1, logRepository.findAll().size());
        assertFalse(lockRepository.findAll().get(0).isActive());
        assertTrue(updated);
        Mockito.verify(lockRepository, Mockito.times(2)).acquireLock(anyString(), anyInt());
    }

    @Test
    void testAwaitAndIgnoreExecution() throws Exception {
        // given
        RegistryTaskLock existingLock = lockRepository.findAll().get(0);
        existingLock.setActive(true);
        existingLock.setAcquiredBy("me");
        existingLock.setAcquiredAt(new Date());
        lockRepository.save(existingLock);

        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.schedule(this::insertCurrentResourceIdentifier, 1500, TimeUnit.MILLISECONDS);
        scheduler.shutdown();

        Consumer<Resource> consumer = getConsumer();

        // when
        lockFacade.execute(resource, consumer);

        // then
        assertEquals(1, logRepository.findAll().size());
        assertFalse(updated);
        Mockito.verify(lockRepository, Mockito.times(2)).acquireLock(anyString(), anyInt());
    }

    private Consumer<Resource> getConsumer() {
        Consumer<Resource> consumer = r -> {
            try {
                updated = true;
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        return consumer;
    }

    private void insertCurrentResourceIdentifier() {
        Object identifier = ReflectionTestUtils.invokeMethod(lockFacade, "calculateIdentifier", resource);
        RegistryTaskLog taskLog = new RegistryTaskLog();
        taskLog.setId(identifier.toString());
        taskLog.setExecutedDate(new Date());
        logRepository.save(taskLog);
    }
}
