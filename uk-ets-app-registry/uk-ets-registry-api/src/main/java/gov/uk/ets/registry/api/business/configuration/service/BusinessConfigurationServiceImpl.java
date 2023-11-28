package gov.uk.ets.registry.api.business.configuration.service;

import gov.uk.ets.registry.api.business.configuration.domain.ApplicationPropertyEnum;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * This service is responsible for business configuration actions
 * such as to retrieve the values from the application property file
 */
@Service
@AllArgsConstructor
public class BusinessConfigurationServiceImpl implements BusinessConfigurationService {

    private final Environment env;

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getApplicationProperties() throws IOException {

        Map<String, String> allPropertiesMap = new HashMap<>();

        final Properties properties = readAllProperties();
        properties.forEach((key, value) -> {
            Optional<ApplicationPropertyEnum> applicationPropertyEnum = Arrays.stream(ApplicationPropertyEnum.values())
                    .filter(property -> key.toString().equals(property.getKey()))
                    .findFirst();
            if(applicationPropertyEnum.isPresent())
                allPropertiesMap.put(key.toString(), value.toString());
        });

        return allPropertiesMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getApplicationPropertyByKey(String key) throws IOException {

        final Properties properties = readAllProperties();
        String value = properties.getProperty(key);

        Optional<ApplicationPropertyEnum> applicationPropertyEnum = Arrays.stream(ApplicationPropertyEnum.values())
                .filter(property -> key.equals(property.getKey()))
                .findFirst();

        Map<String, String> applicationPropertiesMap = new HashMap<>();
        if(applicationPropertyEnum.isPresent())
            applicationPropertiesMap.put(key, value);

        return applicationPropertiesMap;
    }

    /**
     * Reads the application properties file.
     *
     * @return {@link Properties}
     * @throws IOException
     */
    private Properties readAllProperties() throws IOException {
        final Properties properties = readApplicationProperties();
        properties.putAll(readMailProperties());
        return properties;
    }
    
    /**
     * Reads the application properties file.
     *
     * @return {@link Properties}
     * @throws IOException
     */
    private Properties readApplicationProperties() throws IOException {
        return readProperties("application.properties");
    }
    
    /**
     * Reads the application properties file.
     *
     * @return {@link Properties}
     * @throws IOException
     */
    private Properties readMailProperties() throws IOException {
        return readProperties("mail.properties");
    }
    
    /**
     * Reads the application properties file.
     *
     * @return {@link Properties}
     * @throws IOException
     */
    private Properties readProperties(String fileName) throws IOException {
        final Properties properties = new Properties();
        ClassLoader loader = this.getClass().getClassLoader();
        InputStream resourceStream = loader.getResourceAsStream(fileName);
        properties.load(resourceStream);
        properties.entrySet().forEach( k-> {
            String envValue = env.getProperty(k.getKey().toString());
            if (envValue != null) {
                k.setValue(envValue);
            }
        });
        return properties;
    }
}
