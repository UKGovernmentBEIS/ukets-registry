package gov.uk.ets.registry.api.notification.userinitiated.services;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.uk.ets.registry.api.notification.emailgeneration.EmailException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

@Service
@RequiredArgsConstructor
public class StringEmailTemplateProcessor {
    private static final String ERROR_PROCESSING_TEMPLATE = "Error while processing the template";

    private final Configuration freemarkerConfiguration;

    public String processTemplate(String templateStr, Map<String, Object> params) {
        String content;
        try {
            // this is an expensive operation but there is probably no need to cache the template here since
            // it can change at any moment
            Template t = new Template("name", new StringReader(templateStr), freemarkerConfiguration);
            content = FreeMarkerTemplateUtils.processTemplateIntoString(t, params);
        } catch (IOException | TemplateException e) {
            throw new EmailException(ERROR_PROCESSING_TEMPLATE, e);
        }
        return content;
    }
}
