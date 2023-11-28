package uk.gov.ets.itl.webservices.client;

import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
	"INVOKING_SERVICE_APPLICATION_PORT=${random.int}",
	"INVOKING_SERVICE_TL_SERVICE_URL=http://localhost:7701/itl-ws/services/TransactionLogPort",
	"INVOKING_SERVICE_TL_SERVICE_USERNAME=${random.value}",
	"INVOKING_SERVICE_TL_SERVICE_PASSWORD=${random.value}",
	"INVOKING_SERVICE_KAFKA_BOOTSTRAP_SERVERS=localhost:9092",
	"kafka.authentication.enabled=false",
	"kafka.client.username=test",
	"kafka.client.password=test",
})
@SpringBootTest
class ItlInvokingServiceApplicationTests {

	@Test
	void contextLoads() {
		assertTrue(true);
	}

}
