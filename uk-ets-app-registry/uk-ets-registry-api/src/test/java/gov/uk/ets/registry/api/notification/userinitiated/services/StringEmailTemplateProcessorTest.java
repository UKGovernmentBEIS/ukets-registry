package gov.uk.ets.registry.api.notification.userinitiated.services;

import static org.junit.Assert.assertThrows;

import gov.uk.ets.registry.api.notification.emailgeneration.EmailException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {StringEmailTemplateProcessor.class, FreeMarkerConfigurationFactoryBean.class})
class StringEmailTemplateProcessorTest {

    @Autowired
    private StringEmailTemplateProcessor cut;

    @Test
    void shouldThrowIFVariableNameIsWrong() {

        assertThrows(EmailException.class,
            () -> cut.processTemplate("${variable}", Map.of("var", "value")));
    }

}
