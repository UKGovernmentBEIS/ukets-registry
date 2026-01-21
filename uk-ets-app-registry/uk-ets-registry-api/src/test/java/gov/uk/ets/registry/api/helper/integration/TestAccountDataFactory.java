package gov.uk.ets.registry.api.helper.integration;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class TestAccountDataFactory {

    private static final AtomicLong aviationCounter = new AtomicLong(30000000);
    private static final AtomicLong installationCounter = new AtomicLong(40000000);
    private static final AtomicLong maritimeCounter = new AtomicLong(50000000);


    public TestAccountData nextAviationAccount() {
        long base = aviationCounter.incrementAndGet();

        return new TestAccountData(
                String.valueOf(base),   // accountIdentifier
                base,                   // accountId
                base + 1000,            // holderId
                base + 2000,            // operatorId (aircraftOperatorId)
                base + 3000             // holderIdentifier
        );
    }

    public TestAccountData nextInstallationAccount() {
        long base = installationCounter.incrementAndGet();

        return new TestAccountData(
                String.valueOf(base),
                base,
                base + 1000,
                base + 2000,            // installationId
                base + 3000
        );
    }

    public TestAccountData nextMaritimeAccount() {
        long base = maritimeCounter.incrementAndGet();

        return new TestAccountData(
                String.valueOf(base),
                base,
                base + 1000,
                base + 2000,            // maritimeOperatorId
                base + 3000                    // maritime δεν έχει holderIdentifier
        );
    }
}
