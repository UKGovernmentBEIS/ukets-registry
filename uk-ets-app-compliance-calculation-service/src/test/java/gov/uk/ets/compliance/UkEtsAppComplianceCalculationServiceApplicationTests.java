package gov.uk.ets.compliance;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("until we fix the issue in the jenkins integration server and postgresql db")
class UkEtsAppComplianceCalculationServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
