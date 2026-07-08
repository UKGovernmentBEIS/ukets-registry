package gov.uk.ets.registry.api.account.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountClaimDTO {

    @NotBlank
    @Size(max = 12, message = "Account Claim Code must not exceed 12 characters.")
    private String accountClaimCode;

    @NotBlank
    private String registryId;

    @JsonIgnore
    public Long getRegistryIdAsLong() {
        try {
            return Long.parseLong(registryId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Registry ID must be a valid numeric value: " + registryId);
        }
    }
}
