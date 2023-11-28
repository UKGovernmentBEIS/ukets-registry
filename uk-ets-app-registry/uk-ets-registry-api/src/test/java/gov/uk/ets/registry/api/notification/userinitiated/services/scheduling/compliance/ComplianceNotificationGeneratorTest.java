package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.compliance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.uk.ets.registry.api.notification.emailgeneration.EmailException;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.AircraftOperatorParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.BaseNotificationParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.InstallationParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import gov.uk.ets.registry.api.notification.userinitiated.services.StringEmailTemplateProcessor;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ComplianceNotificationGenerator.class, StringEmailTemplateProcessor.class,
    FreeMarkerConfigurationFactoryBean.class})
class ComplianceNotificationGeneratorTest {


    private static final String FIRST_NAME = "UserFirst";
    private static final String LAST_NAME = "UserLast";
    private static final String AH_FIRST_NAME = "AhFirst";
    private static final String AH_LAST_NAME = "AhLast";
    private static final String AH_NAME = "AhName";
    private static final String INSTALLATION_NAME = "InstallationName";
    private static final String PERMIT_ID = "PermitId";
    private static final String MONITORING_PLAN = "MonitoringPlan";
    private static final Long OPERATOR_ID = 1L;

    private static final String TEMPLATE = "For first name, enter ${user.firstName}\n" +
        "\n" +
        "For last name, enter ${user.lastName}\n" +
        "\n" +
        "For installation account holder, enter ${accountHolder.name}\n" +
        "\n" +
        "For installation Permit ID, enter ${installation.permitId}\n" +
        "\n" +
        "For installation name, enter ${installation.name}\n" +
        "\n" +
        "For aviation monitoring plan, enter ${operator.monitoringPlan}\n" +
        "\n" +
        "For aviation Operator ID, enter ${operator.id}\n" +
        "\n" +
        "For Balance of compliance obligations or surrenders, enter ${balance}\n" +
        "\n" +
        "For current date obligations or surrenders, enter ${currentDate}";
    public static final String TEST_EMAIL = "test@test.com";

    @Autowired
    private ComplianceNotificationGenerator cut;


    NotificationParameterHolder parameterHolder;
    NotificationDefinition definition;
    Date now;

    @BeforeEach
    void setUp() {

        now = new Date();

        parameterHolder = NotificationParameterHolder.builder()
            .email(TEST_EMAIL)
            .baseNotificationParameters(BaseNotificationParameters.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .accountHolderFirstName(AH_FIRST_NAME)
                .accountHolderLastName(AH_LAST_NAME)
                .accountHolderName(AH_NAME)
                .currentDate(now)
                .build())
            .installationParameters(InstallationParameters.builder()
                .name(INSTALLATION_NAME)
                .permitId(PERMIT_ID)
                .build())
            .aircraftOperatorParameters(AircraftOperatorParameters.builder()
                .monitoringPlan(MONITORING_PLAN)
                .id(OPERATOR_ID)
                .build())
            .balance(18L)
            .build();

        definition = NotificationDefinition.builder()
            .shortText("subject")
            .longText(TEMPLATE)
            .build();
    }

    @Test
    void shouldParseTemplateParams() {


        IdentifiableEmailNotification email = cut.generate(parameterHolder, definition);

        assertThat(email).isNotNull();
        assertThat(email.getRecipients()).containsExactly(TEST_EMAIL);
        assertThat(email.getBodyHtml()).contains(FIRST_NAME, LAST_NAME, AH_FIRST_NAME, AH_LAST_NAME, INSTALLATION_NAME,
            PERMIT_ID, MONITORING_PLAN, OPERATOR_ID.toString(), now.toString());
    }

    @Test
    void shouldSetEmptyValueWhenSimpleParameterIsNull() {

        parameterHolder.setBalance(null);

        IdentifiableEmailNotification email = cut.generate(parameterHolder, definition);

        assertThat(email.getBodyHtml()).doesNotContain("enter ${balance}");
        assertThat(email.getBodyHtml()).contains("enter ");
    }

    @Test
    void shouldThrowWhenComplexParameterIsNull() {

        parameterHolder.setInstallationParameters(null);

        assertThrows(EmailException.class, () -> cut.generate(parameterHolder, definition));

    }
}
