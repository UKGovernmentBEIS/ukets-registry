package gov.uk.ets.registry.api.file.upload.clamav;

import static org.junit.jupiter.api.Assertions.*;


import gov.uk.ets.registry.api.common.error.UkEtsException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.CommunicationException;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

@Disabled
class ClamavServiceTest {


    public static final String CLAMAV_DAEMON_SERVER_HOST="localhost";
    
    @Test
    void testFile() throws IOException {
        Resource resource = new ClassPathResource("clamav/test2.txt");
        ClamavClient client = new ClamavClient(CLAMAV_DAEMON_SERVER_HOST);
        ScanResult scanResult = client.scan(resource.getInputStream());
        if (scanResult instanceof ScanResult.OK) {
            assertTrue(true);
        } else if (scanResult instanceof ScanResult.VirusFound) {
            Map<String, Collection<String>> viruses = ((ScanResult.VirusFound) scanResult).getFoundViruses();
            assertFalse(viruses.isEmpty());
        }
    }

    @Test
    void testFileWithVirus() {
        InputStream stream = new ByteArrayInputStream("X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*".getBytes(StandardCharsets.UTF_8));
        ClamavClient client = new ClamavClient(CLAMAV_DAEMON_SERVER_HOST);
        ScanResult scanResult;
        try {
            scanResult = client.scan(stream);
            assertNotNull(scanResult);
        } catch( CommunicationException e) {
            throw new UkEtsException(e);
        }
        if (scanResult instanceof ScanResult.OK) {
            assertTrue(true);
        } else if (scanResult instanceof ScanResult.VirusFound) {
            Map<String, Collection<String>> viruses = ((ScanResult.VirusFound) scanResult).getFoundViruses();
            assertFalse(viruses.isEmpty());
        }
    }
}
