package gov.uk.ets.registry.api.payment.service.integration.exception;

import lombok.Getter;

@Getter
public class WebLinkPaymentAlreadyCompletedException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String returnUrl;
    
    public WebLinkPaymentAlreadyCompletedException(String returnUrl) {
        super();
        this.returnUrl = returnUrl;
    }
    
}
