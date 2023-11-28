package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import gov.uk.ets.registry.usernotifications.MultipartEmailWithSubject;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

/**
 * Generates emails based on the concept of sentences.
 */
public abstract class EmailGenerator {

    private EmailSentence subjectSentence;

    private static final String ERROR_PROCESSING_TEMPLATE = "Error while processing the template";

    abstract Map<String, Object> params();

    abstract String htmlTemplate();

    abstract String textTemplate();

    abstract Configuration freemarkerConfiguration();

    /**
     * The subject.
     *
     * @param subjectSentence the subject
     * @return the generator.
     */
    EmailGenerator subject(EmailSentence subjectSentence) {
        this.subjectSentence = subjectSentence;
        return this;
    }

    /**
     * Generate the email text.
     *
     * @return the email text
     */
    public MultipartEmailWithSubject generate() {
        return new MultipartEmailWithSubject() {
            @Override
            public String subject() {
                return subjectSentence.generate();
            }

            @Override
            public String bodyHtml() {
                return getEmailContent(freemarkerConfiguration(), htmlTemplate(), params());
            }

            @Override
            public String bodyPlain() {
                return getEmailContent(freemarkerConfiguration(), textTemplate(), params());
            }
        };
    }

    /**
     * Utility for getting the last characters of a string.
     *
     * @param str input string
     * @return the last two chars
     */
    public String lastTwo(String str) {
        return str.substring(str.length() - 2);
    }

    /**
     * Utility for getting the lowercase version of a string
     *
     * @param str the input string
     * @return the lowercase version
     */
    public String lowercase(String str) {
        return str != null ? str.toLowerCase() : null;
    }

    /**
     * This class is the backbone of the Email.
     */
    public static class EmailSentence {
        String sentence;
        Object[] variables;

        /**
         * Constructor.
         *
         * @param sentence  the actual textual sentence with variables. E.g. "Hello {0}"
         * @param variables the values of the variables
         */
        public EmailSentence(String sentence, Object... variables) {
            this.sentence = sentence;
            this.variables = variables;
        }

        /**
         * Combine a sentence and the variables and returns the actual text
         *
         * @return
         */
        public String generate() {
            return MessageFormat.format(sentence, variables);
        }
    }

    /**
     * Method for generating the email content
     *
     * @param freemarkerConfiguration the input string
     * @param templatePath the template path with the template filename
     * @param params a map with template attributes
     * @return the lowercase version
     */
    public String getEmailContent(Configuration freemarkerConfiguration, String templatePath,
                                  Map<String, Object> params) {
        Template t;
        String content;

        try {
            t = freemarkerConfiguration.getTemplate(templatePath);
            content = FreeMarkerTemplateUtils.processTemplateIntoString(t, params);
        } catch (TemplateException | IOException  exception) {
            throw new EmailException(ERROR_PROCESSING_TEMPLATE, exception);
        }
        return content;
    }
}
