package uk.gov.ets.kp.webservices.registry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
class RegistryServiceAspectTest {

    RegistryServiceAspect aspect = new RegistryServiceAspect();
    Exception expectedException = new Exception("test exception message");

    @Test
    void afterThrowingAdviceShouldLogTheException(CapturedOutput output) {
        try {
            aspect.afterThrowingAdvice(expectedException);
            assertThat(output).contains(expectedException.getMessage());
        } catch(Exception exception) {}
    }

    @Test
    void afterThrowingAdviceShouldThrowTheException() {
        assertThrows(IllegalStateException.class, () -> aspect.afterThrowingAdvice(expectedException));
    }
}