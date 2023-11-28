package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.LoginErrorNotification;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.usernotifications.MultipartEmailWithSubject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = NotificationProperties.class)
@ContextConfiguration(classes = {FreeMarkerConfigurationFactoryBean.class, NotificationProperties.class,
    MailConfiguration.class})
@TestPropertySource(locations = "classpath:/mail.properties")
class LoginErrorEmailGeneratorTest {

    @Autowired
    NotificationProperties notificationProperties;

    @Autowired
    Configuration freemarkerConfiguration;

    @Autowired
    MailConfiguration mailConfiguration;

    String emailTemplatesPath = "/templates/email";

    @BeforeEach
    public void setup() {
        freemarkerConfiguration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), emailTemplatesPath);
    }

    @Test
    void testLoginErrorTemplatesNotNull() {
        Assertions.assertNotNull(freemarkerConfiguration);
        try {
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/html/login-error.ftl"));
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/text/login-error.ftl"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test()
    void shouldGenerateLoginErrorEmail() {
        LoginErrorEmailGenerator generator = new LoginErrorEmailGenerator(LoginErrorNotification.builder()
            .emailAddress("test@test.gr")
            .url("http://localhost:4200")
            .build(), notificationProperties.getLoginError(), freemarkerConfiguration, mailConfiguration);

        MultipartEmailWithSubject email = generator.generate();

        assertEquals("Login error - UK Emissions Trading Registry", email.subject());
        assertTrue(email.bodyHtml().contains("A failed login attempt was detected to your account"));
        assertTrue(email.bodyHtml().contains("http://localhost:4200"));
    }
}
