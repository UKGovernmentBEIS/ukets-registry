package gov.uk.ets.registry.api.user.admin.web.model;

import gov.uk.ets.registry.api.user.domain.AgentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAgentUpdateDTO {

    /**
     * Agent type.
     */
    @NotNull
    private AgentType agent;

    /**
     * The agent company name.
     */
    @Size(max = 256, message = "Company name must not exceed 256 characters.")
    private String agentCompanyName;

    /**
     * The email address.
     */
    @Email
    @Size(max = 256, message = "Email must not exceed 256 characters.")
    private String agentEmailAddress;

    /**
     * The country code of the phone number.
     */
    @Size(max = 10, message = "Country code must not exceed 10 characters.")
    private String agentPhoneNumberCountryCode;

    /**
     * The phone number.
     */
    @Size(max = 256, message = "Phone number must not exceed 256 characters.")
    private String agentPhoneNumber;

}
