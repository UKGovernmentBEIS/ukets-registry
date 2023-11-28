package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import java.util.Map;

public class DoNothingEmailGenerator extends EmailGenerator{
    @Override
    Map<String, Object> params() {
        return null;
    }

    @Override
    String htmlTemplate() {
        return null;
    }

    @Override
    String textTemplate() {
        return null;
    }

    @Override
    Configuration freemarkerConfiguration() {
        return null;
    }
}
