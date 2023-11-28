package gov.uk.ets.compliance;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.compliance.domain.DynamicComplianceCalculator;
import gov.uk.ets.compliance.repository.DynamicComplianceRepository;
import gov.uk.ets.compliance.service.DynamicComplianceService;
import gov.uk.ets.compliance.service.StaticComplianceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ComplianceConfig {

    @Bean
    public DynamicComplianceCalculator dynamicComplianceCalculator() {
        return new DynamicComplianceCalculator();
    }

    @Bean
    public DynamicComplianceService dynamicComplianceService(
            DynamicComplianceRepository dynamicComplianceRepository, ObjectMapper objectMapper) {
        return new DynamicComplianceService(dynamicComplianceRepository, objectMapper);
    }

    @Bean
    public StaticComplianceService staticComplianceService(
            DynamicComplianceRepository dynamicComplianceRepository, ObjectMapper objectMapper) {
        return new StaticComplianceService(dynamicComplianceRepository, objectMapper);
    }

}

