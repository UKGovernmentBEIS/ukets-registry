package uk.gov.ets.user.feedback.api.web.model;

import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFeedbackRequest {

	@Size(max = 50)
	private String timestamp;

	@Size(max = 50)
	private String satisfactionRate;

	@Size(max = 50)
	private String userRegistrationRate;

	@Size(max = 50)
	private String onlineGuidanceRate;

	@Size(max = 50)
	private String creatingAccountRate;

	@Size(max = 50)
	private String onBoardingRate;

	@Size(max = 50)
	private String tasksRate;

	@Size(max = 1200)
	private String improvementSuggestion;	
			
}
