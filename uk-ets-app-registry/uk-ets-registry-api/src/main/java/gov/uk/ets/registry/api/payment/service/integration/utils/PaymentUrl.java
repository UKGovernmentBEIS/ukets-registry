package gov.uk.ets.registry.api.payment.service.integration.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * This class is responsible for 2 things.
 * 
 * <ul>
 * <li>Generate the <strong>return</strong> urls for both the pay by card and pay via weblink cases.</li>
 * <li>Generate the weblink url for the case of payment via weblink.</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class PaymentUrl {

    private final UkGovPaymentProperties paymentProperties;
    
    
    public String generatePaymentReturnUrl(Long referenceNumber) {
        return String.format(paymentProperties.getReturnUrl(),
                URLEncoder.encode(String.valueOf(referenceNumber), StandardCharsets.UTF_8));
    }
    
    /**
     * Generates the payment link url.
     * 
     * @param urlSuffix the uuid of the payment
     * @return the payment via weblink url
     */
    public String generatePaymentWebLinkUrl(String urlSuffix) {
        return String.format(paymentProperties.getWeblink().getUrl(),
                URLEncoder.encode(urlSuffix, StandardCharsets.UTF_8));
    }

    /**
     * Generates the payment link return url.
     * 
     * @param urlSuffix the uuid of the payment
     * @return the payment via weblink return url
     */
    public String generatePaymentWebLinkReturnUrl(String urlSuffix) {
        return String.format(paymentProperties.getWeblink().getReturnUrl(),
                URLEncoder.encode(urlSuffix, StandardCharsets.UTF_8));
    }
}
