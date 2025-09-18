package gov.uk.ets.registry.api.common;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CountryMap {

    private final Mapper mapper;

    @Value("classpath:data/countries.json")
    private Resource countriesResource;

    private Map<String, String> countries;

    @PostConstruct
    public void init() {
        countries = mapper.convertResToPojo(countriesResource, new TypeReference<>() { });
    }

    public String getCountryName(String code) {
        return countries.get(code);
    }

    public boolean containsCountryCode(String code) {
        return countries.containsKey(code);
    }
}
