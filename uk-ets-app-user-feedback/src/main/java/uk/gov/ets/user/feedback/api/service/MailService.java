package uk.gov.ets.user.feedback.api.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import uk.gov.ets.user.feedback.api.web.error.UkEtsUserFeedbackException;
import uk.gov.ets.user.feedback.api.web.model.UserFeedbackRequest;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;

@Log4j2
@Service
public class MailService {

	/**
     * The mail sender
     */
    protected final JavaMailSender mailSender;
    
    /**
     * The template engine
     */
    private ITemplateEngine templateEngine;
    
    /**
     * The mail configuration
     */
    protected final MailConfiguration mailConfiguration;

    public MailService(JavaMailSender mailSender, MailConfiguration mailConfiguration, ITemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.mailConfiguration = mailConfiguration;
        this.templateEngine = templateEngine;
    }
    

    
    /**
     * Sends an email with the provided parameters
     * 
     * @param userFeedbackRequest UserFeedbackRequest
     */
    public void sendMail(UserFeedbackRequest userFeedbackRequest) {
    	
    	String satisfactionRate = userFeedbackRequest.getSatisfactionRate();    	
    	String userRegistrationRate = userFeedbackRequest.getUserRegistrationRate();
    	String onlineGuidanceRate = userFeedbackRequest.getOnlineGuidanceRate();
    	String creatingAccountRate = userFeedbackRequest.getCreatingAccountRate();
    	String onBoardingRate = userFeedbackRequest.getOnBoardingRate();
    	String tasksRate = userFeedbackRequest.getTasksRate();
    	String improvementSuggestion = userFeedbackRequest.getImprovementSuggestion();
    	
    	if ( StringUtils.isEmpty(satisfactionRate) &&
    		 StringUtils.isEmpty(userRegistrationRate) &&
    		 StringUtils.isEmpty(onlineGuidanceRate) &&
    		 StringUtils.isEmpty(creatingAccountRate) &&
    		 StringUtils.isEmpty(onBoardingRate) &&
    		 StringUtils.isEmpty(tasksRate) &&
    		 StringUtils.isEmpty(improvementSuggestion)) {
    		throw new UkEtsUserFeedbackException("satisfactionRate or improvementSuggestion is required");
    	}
    	    	    	    
    	Context context = new Context();
        context.setVariable("userFeedbackRequest", userFeedbackRequest);
        
        String process = templateEngine.process("emails/survey", context);
    	
    	String[] toAddresses = mailConfiguration.getToAddress().split(",");
        log.debug("Sending mail with subject {} to: {}", mailConfiguration.getSubject(), Arrays.toString(toAddresses));
        for (String toAddress : toAddresses) {
            try {
            	
                MimeMessage msg = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(msg, true);

                helper.setFrom(mailConfiguration.getFromAddress());
                helper.setReplyTo(mailConfiguration.getFromAddress());
                helper.setTo(toAddress);
                helper.setSubject(mailConfiguration.getSubject());
                helper.setText(process, true);

                mailSender.send(msg);
                
                log.debug("Sent mail with subject {} to: {}", mailConfiguration.getSubject(), toAddress);
            } catch (MessagingException ex) {
                log.error("The message with subject {} could not be sent to {}", mailConfiguration.getSubject(), toAddress, ex);
            }
        }
    }
    
}
