package gov.uk.ets.file.upload.services;

import gov.uk.ets.commons.logging.SecurityLog;
import gov.uk.ets.file.upload.error.ClamavException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

@Service
@Log4j2
public class ClamavService {

    @Value("${clamav.server.hostname:localhost}")
    private String clamavServerHostName;

    @Value("${clamav.server.port:3310}")
    private int clamavServerPort;

    public static final String CLAMAV_EXCEPTION = "Exception while scanning for viruses. " +
                                                  "ClamAV service is not reachable.";

    /**
     * Scan for viruses using clamav antivirus.
     *
     * @param inputStream the inputStream
     * @throws ClamavException if the file contains viruses
     */
    public void scan(InputStream inputStream) {
        ClamavClient client = new ClamavClient(clamavServerHostName, clamavServerPort);
        ScanResult scanResult;
        try {
            scanResult = client.scan(inputStream);
        } catch (xyz.capybara.clamav.ClamavException e) {
            SecurityLog.log(log, "Exception while scanning for viruses." ,e);
            throw new ClamavException(CLAMAV_EXCEPTION);
        }
        if (scanResult instanceof ScanResult.OK) {
            log.debug("File scanned successfully");
        } else if (scanResult instanceof ScanResult.VirusFound) {
            SecurityLog.log(log, "The selected file contains a virus");
            Map<String, Collection<String>> viruses = ((ScanResult.VirusFound) scanResult).getFoundViruses();
            viruses.forEach((k, v) -> log.warn("Found virus:{}/{}", k, v));
            throw new ClamavException("The selected file contains a virus");
        }
    }
}
