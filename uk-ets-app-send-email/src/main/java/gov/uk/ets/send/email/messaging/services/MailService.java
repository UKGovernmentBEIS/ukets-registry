package gov.uk.ets.send.email.messaging.services;

import gov.uk.ets.send.email.messaging.config.MailConfiguration;
import java.io.IOException;
import java.util.Arrays;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class MailService {

    @Value("classpath:images/signature-logo.png")
    Resource signatureImage;

    /**
     * The mail sender
     */
    protected final JavaMailSender mailSender;
    /**
     * The mail configuration
     */
    protected final MailConfiguration mailConfiguration;

    public MailService(JavaMailSender mailSender, MailConfiguration mailConfiguration) {
        this.mailSender = mailSender;
        this.mailConfiguration = mailConfiguration;
    }

    /**
     * Sends an email with the provided parameters
     *
     * @param toAddresses the To addresses of the email
     * @param subject     the Subject of the email
     * @param htmlText    the html text of the email
     * @param plainText   the plain text of the email
     * @param bcc the bcc recipient
     */
    public void sendMail(String[] toAddresses, String subject, String htmlText, String plainText, String bcc) {
        log.debug("Sending mail with subject {} to: {}", () -> subject, () -> Arrays.toString(toAddresses));
        htmlText = augmentWithSignatureImage(htmlText);
        for (String toAddress : toAddresses) {
            try {
                MimeMessage msg = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(msg, true);

                helper.setFrom(mailConfiguration.getFromAddress());
                helper.setTo(toAddress);
                if (bcc != null) {
                    helper.setBcc(bcc);
                }
                helper.setSubject(subject);
                helper.setText(plainText, htmlText);
                // link to signature file
                helper.addInline("signatureImage", signatureImage);

                mailSender.send(msg);
                log.debug("Sent mail with subject {} to: {}", subject, toAddress);
            } catch (MessagingException ex) {
                log.error("The message with subject {} could not be sent to {}", subject, toAddress, ex);
            }
        }
    }

    private String augmentWithSignatureImage(String htmlText) {
        htmlText += "<img src='cid:signatureImage'/>";
        return htmlText;
    }
}
