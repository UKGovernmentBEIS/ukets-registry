package gov.uk.ets.registry.api.lock;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;
import javax.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * This is the Lock mechanism Facade.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class RegistryLockFacade {

    private static final long RETRY_DELAY_MILLISECONDS = 1000L;
    private static final int MAX_RETRIES = 120;
    private final RegistryTaskService service;

    /**
     * Tries to execute a task based on a Resource.
     * If the latest task was executed with the same resource, it will return immediately.
     *
     * @param resource provided resource
     * @param consumer provided consumer with task logic
     * @return true if the consumer is executed, false otherwise
     * @throws Exception
     */
    public boolean execute(Resource resource, Consumer<Resource> consumer) throws Exception {

        String identifier = calculateIdentifier(resource);
        if (shouldExecute(identifier)) {
            log.info("Got the lock...");
            consumer.accept(resource);
            log.info("Task completed! Release lock...");
            releaseLock(identifier);
            return true;
        }
        return false;
    }

    /*
     * Tries to acquire the lock.
     */
    private boolean shouldExecute(String identifier) throws InterruptedException {

        int retries = 0;
        do {
            if (service.sameAsLatest(identifier)) {
                return false;
            }
            if (retries++ > MAX_RETRIES) {
                throw new IllegalStateException("Unable to acquire lock. Maximum retries has been reached.");
            }
            log.info("Trying to acquire Registry task lock. Attempt: {}", retries);
        } while (!acquireLockOrWait());

        return true;
    }

    /*
     * Tries to acquire the lock. If it fails, it will wait before returning.
     */
    private boolean acquireLockOrWait() throws InterruptedException {
        if (service.acquireLock()) {
            return true;
        }
        Thread.sleep(RETRY_DELAY_MILLISECONDS);
        return false;
    }

    /*
     * Release the lock and mark the process identifier as done.
     */
    private void releaseLock(String identifier) {
        service.release(identifier);
    }

    /*
     * Calculates the MD5 hash based on the provided resource.
     */
    private String calculateIdentifier(Resource resource) {
        try {
            InputStream inputStream = resource.getInputStream();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(inputStream.readAllBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not calculate resource MD5.", e);
        }
    }
}
