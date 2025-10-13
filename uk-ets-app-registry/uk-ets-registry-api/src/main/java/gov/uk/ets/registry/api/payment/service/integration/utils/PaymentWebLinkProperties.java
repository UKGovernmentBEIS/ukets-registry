package gov.uk.ets.registry.api.payment.service.integration.utils;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentWebLinkProperties {


    @NotBlank
    private String url;

    @NotBlank
    private String returnUrl;
    
    @NotBlank
    private String errorUrl;
    
}
