package uk.gov.ets.user.feedback.api.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.ArgumentCaptor;

import org.mockito.Mock;
import org.mockito.Mockito;;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;



import org.thymeleaf.context.Context;
import uk.gov.ets.user.feedback.api.web.error.UkEtsUserFeedbackException;
import uk.gov.ets.user.feedback.api.web.model.UserFeedbackRequest;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

	public static final String SATISFIED = "SATISFIED";
	public static final String SUGGESTION = "SUGGESTION";
	public static final String TO_ADDRESS = "foo@bar.com";
	public static final String SUBJECT = "Subject";
	public static final String FROM_ADDRESS = "foo@bar.com";
	public static final String TEMPLATE_USER_CREATION = "emails/survey";
	MailService mailService;
	
	@Mock
	JavaMailSender mailSender;

	@Mock
	ITemplateEngine templateEngine;

	@Mock
	MailConfiguration mailConfiguration;
	
	@Mock
	UserFeedbackRequest userFeedbackRequest;

	
	@BeforeEach
    public void setup() {        
		mailService = new MailService(mailSender, mailConfiguration, templateEngine);
    }

	@Test
	public void sendMail_throwsException() {


		Mockito.when(userFeedbackRequest.getSatisfactionRate()).thenReturn(null);
		Mockito.when(userFeedbackRequest.getUserRegistrationRate()).thenReturn(null);
		Mockito.when(userFeedbackRequest.getOnlineGuidanceRate()).thenReturn(null);
		Mockito.when(userFeedbackRequest.getCreatingAccountRate()).thenReturn(null);
		Mockito.when(userFeedbackRequest.getSatisfactionRate()).thenReturn(null);
		Mockito.when(userFeedbackRequest.getOnBoardingRate()).thenReturn(null);
		Mockito.when(userFeedbackRequest.getImprovementSuggestion()).thenReturn(null);

		Assertions.assertThrows(UkEtsUserFeedbackException.class, () -> {
			mailService.sendMail(userFeedbackRequest);
		});

	}

	@Test
	public void sendMail() throws MessagingException {

		ArgumentCaptor<MimeMessage> emailCaptor = ArgumentCaptor.forClass(MimeMessage .class);
		MimeMessage mimeMessage = new MimeMessage((Session) null) ;

		Mockito.when(templateEngine.process(eq(TEMPLATE_USER_CREATION), any(Context.class))).thenReturn("html");
		Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

		ArgumentCaptor<SimpleMailMessage> mailMessageArgumentCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

		Mockito.when(userFeedbackRequest.getSatisfactionRate()).thenReturn(SATISFIED);
		Mockito.when(userFeedbackRequest.getUserRegistrationRate()).thenReturn(SATISFIED);
		Mockito.when(userFeedbackRequest.getOnlineGuidanceRate()).thenReturn(SATISFIED);
		Mockito.when(userFeedbackRequest.getCreatingAccountRate()).thenReturn(SATISFIED);
		Mockito.when(userFeedbackRequest.getSatisfactionRate()).thenReturn(SATISFIED);
		Mockito.when(userFeedbackRequest.getOnBoardingRate()).thenReturn(SATISFIED);
		Mockito.when(userFeedbackRequest.getImprovementSuggestion()).thenReturn(SUGGESTION);

		Mockito.when(mailConfiguration.getToAddress()).thenReturn(TO_ADDRESS);
		Mockito.when(mailConfiguration.getSubject()).thenReturn(SUBJECT);
		Mockito.when(mailConfiguration.getFromAddress()).thenReturn(FROM_ADDRESS);

		mailService.sendMail(userFeedbackRequest);

		Mockito.verify(mailSender, times(1)).createMimeMessage();
		Mockito.verify(mailSender, times(1)).send(mimeMessage);
		Mockito.verify(mailSender, times(1)).send(emailCaptor.capture());

		List<MimeMessage> actualList = emailCaptor.getAllValues();
		assertTrue(actualList.size() == 1);
		assertEquals(FROM_ADDRESS, actualList.get(0).getFrom()[0].toString());
		assertEquals(SUBJECT, actualList.get(0).getSubject());

	}
	
}
