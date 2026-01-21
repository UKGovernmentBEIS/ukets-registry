package gov.uk.ets.registry.api.integration.consumer;

import gov.uk.ets.registry.api.common.CountryMap;
import gov.uk.ets.registry.api.integration.service.account.validators.CommonAccountValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ValidatorConfiguration {

    private final CountryMap countryMap;

    @Bean("createAccountValidator")
    public CommonAccountValidator createAccountValidator() {
        return new CommonAccountValidator(
                ValidatorOperationType.CREATE_ACCOUNT,
                countryMap
        );
    }

    @Bean("updateAccountValidator")
    public CommonAccountValidator updateAccountValidator() {
        return new CommonAccountValidator(
                ValidatorOperationType.UPDATE_ACCOUNT,
                countryMap
        );
    }

    @Bean("metsContactsValidator")
    public CommonAccountValidator metsContactsValidator() {
        return new CommonAccountValidator(
                ValidatorOperationType.METS_CONTACTS,
                countryMap
        );
    }
}
