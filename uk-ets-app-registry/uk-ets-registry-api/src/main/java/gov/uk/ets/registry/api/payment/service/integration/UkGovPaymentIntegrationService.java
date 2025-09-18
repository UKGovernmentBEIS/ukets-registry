package gov.uk.ets.registry.api.payment.service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.payment.service.integration.exception.PaymentIntegrationRequestException;
import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationCreateDTO;
import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationCreatedResponseDTO;
import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationErrorResponseDTO;
import gov.uk.ets.registry.api.payment.service.integration.model.PaymentIntegrationStatusResponseDTO;
import gov.uk.ets.registry.api.payment.service.integration.utils.UkGovPaymentProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class UkGovPaymentIntegrationService {

    private final UkGovPaymentProperties paymentProperties;

    private final ObjectMapper objectMapper;

    public PaymentIntegrationCreatedResponseDTO createPayment(PaymentIntegrationCreateDTO paymentDto) throws Exception {
    	
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(paymentProperties.getApiUrl());

            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + paymentProperties.getApiKey());

            String jsonBody = objectMapper.writeValueAsString(paymentDto);
            post.setEntity(new StringEntity(jsonBody));

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());

                if (statusCode >= 200 && statusCode < 300) {
                    return objectMapper.readValue(responseBody, PaymentIntegrationCreatedResponseDTO.class);
                } else {
                    // Handle error response
                    PaymentIntegrationErrorResponseDTO error = objectMapper.readValue(
                            responseBody, PaymentIntegrationErrorResponseDTO.class
                    );

                    throw new PaymentIntegrationRequestException(
                            "Payment creation failed: " + error.getCode() + " - " + error.getDescription(),
                            error
                    );
                }
            }
        }
    }

    public PaymentIntegrationStatusResponseDTO getPaymentStatus(String paymentId) {
        String url = paymentProperties.getApiUrl() +'/'+ paymentId;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.setHeader("Authorization", "Bearer " + paymentProperties.getApiKey());

            try (CloseableHttpResponse response = client.execute(request)) {
                String body = EntityUtils.toString(response.getEntity());
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode >= 200 && statusCode < 300) {
                    return objectMapper.readValue(body, PaymentIntegrationStatusResponseDTO.class);
                } else {
                    throw new RuntimeException("Failed to get payment: HTTP " + statusCode + " - " + body);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during payment status request: " + e.getMessage(), e);
        }
    }
}
