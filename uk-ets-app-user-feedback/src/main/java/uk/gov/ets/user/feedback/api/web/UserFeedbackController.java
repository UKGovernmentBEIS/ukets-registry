package uk.gov.ets.user.feedback.api.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ets.user.feedback.api.service.MailService;
import uk.gov.ets.user.feedback.api.web.model.UserFeedbackRequest;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api-user-feedback", produces=MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
public class UserFeedbackController {

	private final MailService mailService;
	private final ObjectMapper objectMapper;

	/**
	 * Send an email with the provided parameters
	 *
	 * @param request the values provided for the form
	 */
	@PostMapping(path = "/survey", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Boolean> survey(@Valid UserFeedbackRequest request) {

		MapType mapType = TypeFactory.defaultInstance()
			.constructMapType(HashMap.class, String.class, String.class);
		Map<String, String> userFeedbackRequestMap = objectMapper.convertValue(request, mapType);

		Map<String, String> sanitizedMap = new HashMap<>();
		// all HTML wil be stripped.
		for (Map.Entry<String, String> entry : userFeedbackRequestMap.entrySet()) {
			var sanitizedValue = entry.getValue() != null
				? Jsoup.clean(entry.getValue(), Safelist.none())
				: null;
			sanitizedMap.put(entry.getKey(), sanitizedValue);
		}
		request = objectMapper.convertValue(sanitizedMap, UserFeedbackRequest.class);
		log.debug("userFeedbackRequest: timestamp: {}, satisfactionRate: {}, improvementSuggestion: {},  ",
				request.getTimestamp(), request.getSatisfactionRate(), request.getImprovementSuggestion());

		mailService.sendMail(request);

		return new ResponseEntity<>(true, HttpStatus.OK);
    }

}
