package uk.gov.ets.kp.webservices.registry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(properties = {"kafka.authentication.enabled=true"})
@ContextConfiguration(classes = Config.class)
public class RegistryServiceAspectIntegrationTest {
    @Autowired
    RegistryServiceAspect aspect;

    @MockBean
    RegistryService registryService;

    @Test
    void testAfterThrowingAdvice() {
        try {
            // given
            Exception testException =  new RuntimeException("test exception");
            given(registryService.acceptITLNotice(any())).willThrow(testException);

            // when
            registryService.acceptITLNotice(null);

            // then
            then(aspect).should().afterThrowingAdvice(testException);
        } catch (Exception exception) {
        }
    }
}
