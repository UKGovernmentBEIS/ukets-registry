package gov.uk.ets.registry.api.account.web.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private Long registryId;
}
