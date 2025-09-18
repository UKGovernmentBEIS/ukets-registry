/**
 *
 */
package uk.gov.ets.registration.user.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import gov.uk.ets.commons.logging.SecurityLog;
import java.io.IOException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;

import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ErrorRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import uk.gov.ets.registration.user.UserAttributes;
import uk.gov.ets.registration.user.common.GeneratorService;
import uk.gov.ets.registration.user.exception.EmailVerificationExpiredException;
import uk.gov.ets.registration.user.exception.UserRegistrationException;
import uk.gov.ets.registration.user.exception.UserSubmissionValidationException;
import uk.gov.ets.registration.user.validation.UserRegistrationValidator;
import uk.gov.ets.registration.user.validation.Violation;

/**
 * Provides Keycloak related services.
 */
@Service
@Log4j2
public class UserRegistrationService {

	private static final int FIRST = 0;
	public static final String VERIFY_EMAIL_REQUIRED_ACTION = "VERIFY_EMAIL";
	public static final String CONFIGURE_OTP_REQUIRED_ACTION = "CONFIGURE_TOTP";
	public static final String TERMS_AND_CONDITIONS_ACTION = "terms_and_conditions";
	private KeycloakProps props;
	private Keycloak keycloak;

	@Value("${mail.etrAddress}")
	private String etrAddress;

	@Value("${mail.htmlTemplatesFolder}")
	private String htmlTemplatesFolder;

	@Value("${mail.textTemplatesFolder}")
	private String textTemplatesFolder;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private Configuration freemarkerConfiguration;

	/**
	 * Service for generating the URID.
	 */
	@Autowired
	private GeneratorService generatorService;

	@Autowired
	private UserRegistrationValidator userRegistrationValidator;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;
	
	/**
	 * Default constructor.
	 *
	 * @param props
	 */
	public UserRegistrationService(@Valid KeycloakProps props) {
		this.props = props;
		keycloak = Keycloak.getInstance(props.getServerUrl(), props.getRealm(), props.getUsername(), props.getPasword(),
				props.getClientId());
	}

	/**
	 * Register a user in Keycloak with the provided email.
	 *
	 * @param email
	 * @return a UserRepresentation
	 * @since v.0.1.0
	 */
	public void registerUser(String email) {

		cleanUpExistingPendingRegistration(email);

		UserRepresentation userRepresentation = createInitialUserRepresentation(email);

		try (Response res = keycloak.realm(props.getRealm()).users().create(userRepresentation)) {
			if (HttpStatus.CONFLICT == HttpStatus.valueOf(res.getStatus())) {
				//Email exists , user already registered but continue UKETS-48
				SecurityLog.log(log, "Email exists, user already registered but continuing.");
			} else if (HttpStatus.CREATED != HttpStatus.valueOf(res.getStatus())) {
				throw new UserRegistrationException(res.getStatus());
			}
		}
	}

	/**
	 * Instantiates and initializes a UserRepresentation.
	 * @param email
	 * @return the created UserRepresentation
	 * @since v.0.1.0
	 */
	private UserRepresentation createInitialUserRepresentation(String email) {
		UserRepresentation userRepresentation = new UserRepresentation();
		// Keycloak ignores this setter.
		userRepresentation.setEmail(email);
		// set as User-name the email.
		userRepresentation.setUsername(email);
		userRepresentation.setEmailVerified(false);
		userRepresentation.setEnabled(true);
		userRepresentation.setCreatedTimestamp(System.currentTimeMillis());
		userRepresentation.setRequiredActions(Arrays.asList(VERIFY_EMAIL_REQUIRED_ACTION,CONFIGURE_OTP_REQUIRED_ACTION));

		if (userRepresentation.getAttributes() == null) {
			userRepresentation.setAttributes(new HashMap<>());
		}
		userRepresentation.getAttributes().put(UserAttributes.URID.getName(), Arrays.asList(generatorService.generateURID()));
		userRepresentation.getAttributes().put(UserAttributes.REGISTRATION_IN_PROGRESS.getName(), Arrays.asList(Boolean.TRUE.toString()));

		return userRepresentation;
	}


	/**
	 * Clean up any existing pending registrations upon register to allow
	 * users to re-submit the same email address.
	 * @see JIRA [UKETS-56]
	 * @param email
	 * @since v.0.1.0
	 */
	private void cleanUpExistingPendingRegistration(String email) {
		Optional<UserRepresentation> existingUser =findByEmail(email);
		if(existingUser.isPresent()) {
			Optional<List<String>> registrationAttribute = Optional.ofNullable(existingUser.get().getAttributes().get(UserAttributes.REGISTRATION_IN_PROGRESS.getName()));
			boolean existingRegistrationInProgress =  registrationAttribute.isPresent() ?  Boolean.valueOf(registrationAttribute.get().get(FIRST)) : Boolean.FALSE;
//			User already exists in the Keyclock db in pending registration.
//			Continue by deleting the user and resenting the email.
			if(existingRegistrationInProgress) {
				deleteUser(existingUser.get().getId());
			}
		}
	}

	/**
	 * Finds the user in the Keycloak db with the provided email.
	 *
	 * @param email
	 * @return an Optional of UserRepresentation
	 * @since v.0.1.0
	 */
	public Optional<UserRepresentation> findByEmail(String email) {
		final int first = 0;
		final int last = 1;
		List<UserRepresentation> users = keycloak.realm(props.getRealm()).users().search(email, first, last);
		if(users.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.ofNullable(users.get(FIRST));
		}
	}

	/**
	 * Finds the user in the Keycloak db with the provided id.
	 *
	 * @param userId
	 * @return a UserRepresentation
	 * @since v.0.1.0
	 */
	public UserRepresentation findById(String userId) {
		return keycloak.realm(props.getRealm()).users().get(userId).toRepresentation();
	}

	/**
	 * Deletes a user from keycloak.
	 *
	 * @param userId
	 * @since v.0.1.0
	 */
	public void deleteUser(String userId) {
		keycloak.realm(props.getRealm()).users().delete(userId);
	}

	/**
	 * 
	 * @param userId the keycloak uuid user identifier (e.g. 84baf9b2-75a8-4850-a8f4-a0c5ee9bce54)
	 */
    public void scheduleUnverifiedEmailAndExpiredUserDeletion(String userId) {
        
        taskScheduler.schedule(()-> {
            log.info("Checking user with id from keycloak: {} " , userId);  
            UserRepresentation user = findById(userId);
            if(!user.isEmailVerified().booleanValue()) {
                deleteUser(userId);
                SecurityLog.log(log, "User had not verified email.Deleting : " + userId);
            }
        }, 
            LocalDateTime.now().plusMinutes(props.getVerifyEmailExpiration()).atZone(ZoneId.systemDefault()).toInstant());
     }
	
	/**
	 * Sent an email verification to the user.
	 *
	 * @param userId
	 * @throws MessagingException
	 * @since v.0.1.0
	 */
	public void sendVerifyEmail(String userId, String email) throws MessagingException, TemplateException, IOException {

		MimeMessage msg = javaMailSender.createMimeMessage();
		// true = multipart message
		MimeMessageHelper helper = new MimeMessageHelper(msg, true);
		// From, Subject and Text are getting populated from application.properties UKETS-3052
		helper.setFrom(props.getEmailFrom());
		helper.setTo(email);
		helper.setSubject(props.getEmailSubject());

		final Map<String, Object> params = new HashMap<>();
		params.put("etrAddress", etrAddress);
		params.put("redirectUrl", props.getVerifyEmailRedirectUrl());
		params.put("token", generateToken(userId));

		String htmlText = getTemplatedString(htmlTemplatesFolder + "verify-email.ftl", params);
		String plainText = getTemplatedString(textTemplatesFolder + "verify-email.ftl", params);

		helper.setText(plainText, htmlText);

		javaMailSender.send(msg);
	}

	/**
	 * Generates a encrypted web token (JWT) for use in email verification.
	 * The userId is the subject of the token.
	 *
	 * @param userId
	 * @return the generated web-token
	 * @since v.0.1.0
	 */
	private String generateToken(String userId) {

		Algorithm algorithm = Algorithm.HMAC256(props.getPasword());

		LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
		Date issued = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
		Date expires = Date.from(now.plusMinutes(props.getVerifyEmailExpiration()).atZone(ZoneId.systemDefault()).toInstant());

		return JWT.create().withIssuer(props.getServerUrl())
				.withIssuedAt(issued).withSubject(userId)
				.withAudience(props.getClientId())
				.withExpiresAt(expires)
				.sign(algorithm);

	}

	/**
	 * Validates the token provided.
	 *
	 * @param token
	 * @return the id of the user which was the token subject.
	 * @since v.0.1.0
	 */
	public String validateToken(String token) {
		Algorithm algorithm = Algorithm.HMAC256(props.getPasword());
		JWTVerifier verifier = JWT.require(algorithm).withIssuer(props.getServerUrl()).build(); // Reusable verifier
		try {
			DecodedJWT jwt = verifier.verify(token);
			String userId = jwt.getSubject();
			return userId;
		} catch(TokenExpiredException e) {
			EmailVerificationExpiredException exception = new EmailVerificationExpiredException();
			SecurityLog.log(log, exception.getMessage(), exception.getCause());
			throw exception;
		}

	}

	/**
	 * Updates the user with the provided UserRepresentation.
	 *
	 * @param userRepresentation
	 * @return the updated UserRepresentation
	 * @since v.0.1.0
	 */
	public UserRepresentation update(UserRepresentation userRepresentation) {
		try {
			keycloak.realm(props.getRealm()).users().get(userRepresentation.getId()).update(userRepresentation);
		} catch(BadRequestException e) {
			ErrorRepresentation error = e.getResponse().readEntity(ErrorRepresentation.class);
			Violation violation = new Violation();
			violation.setFieldName("password");
			violation.setMessage(error.getErrorMessage());
			List<Violation> errors = new ArrayList<>();
			errors.add(violation);
			throw new UserSubmissionValidationException(errors);
		}

		return userRepresentation;
	}

	/**
	 * Completes the user registration process be removing the REGISTRATION_IN_PROGRESS flag
	 * and saving the user attributes.
	 *
	 * @param userRepresentation
	 * @return the updated UserRepresentation
	 * @since v.0.1.0
	 */
	@Transactional
	public UserRepresentation completeRegistration(UserRepresentation userRepresentation) {

		userRegistrationValidator.validate(userRepresentation);

		Optional<UserRepresentation> storedUserOptional = findByEmail(userRepresentation.getEmail());
		if(!storedUserOptional.isPresent()) {
			throw new IllegalArgumentException("Illegal user representation");
		}
		UserRepresentation storedUser = storedUserOptional.get();
		if(!storedUser.getAttributes().containsKey(UserAttributes.REGISTRATION_IN_PROGRESS.getName())) {
			return storedUser;
		}
		//Mark user as registered
		userRepresentation.getAttributes().remove(UserAttributes.REGISTRATION_IN_PROGRESS.getName(), Arrays.asList(Boolean.TRUE.toString()));
		userRepresentation.getAttributes().put(UserAttributes.REGISTERED_ON_DATE.getName(),
			List.of(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)));
		update(userRepresentation);

		return userRepresentation;
	}

	/**
	 * Returns the content of an ftl template
	 *
	 * @param ftlLocation the location of ftl template to be used
	 * @return params a map with the ftl variables
	 * @since v.0.1.0
	 */
	public String getTemplatedString(String ftlLocation, Map<String, Object> params) throws TemplateException, IOException{
		Template t = freemarkerConfiguration.getTemplate(ftlLocation);
		return FreeMarkerTemplateUtils.processTemplateIntoString(t, params);
	}
}
