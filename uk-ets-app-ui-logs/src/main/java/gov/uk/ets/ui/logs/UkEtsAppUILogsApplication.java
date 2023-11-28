package gov.uk.ets.ui.logs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.ui.logs.web.JsonSanitizerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UkEtsAppUILogsApplication {

    public static void main(String[] args) {
        SpringApplication.run(UkEtsAppUILogsApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty(name = "features.strict-json-validation.enabled", havingValue = "true")
    public JsonSanitizerService strictSanitizerService() {
        var mapper = new ObjectMapper()
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        return new JsonSanitizerService(mapper, true);
    }

    @Bean
    @ConditionalOnProperty(name = "features.strict-json-validation.enabled", havingValue = "false")
    public JsonSanitizerService sanitizerService() {
        return new JsonSanitizerService(new ObjectMapper(), false);
    }
}
