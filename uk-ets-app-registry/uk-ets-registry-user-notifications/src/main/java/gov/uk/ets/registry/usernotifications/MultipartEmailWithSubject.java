package gov.uk.ets.registry.usernotifications;

/**
 * An email template is a subject and a body.
 */
public interface MultipartEmailWithSubject {

    String subject();

    String bodyHtml();

    String bodyPlain();

    static MultipartEmailWithSubject create(String subject, String bodyHtml, String bodyPlain) {
        return new MultipartEmailWithSubject() {
            @Override
            public String subject() {
                return subject;
            }

            @Override
            public String bodyHtml() {
                return bodyHtml;
            }

            @Override
            public String bodyPlain() {
                return bodyPlain;
            }
        };
    }
}
