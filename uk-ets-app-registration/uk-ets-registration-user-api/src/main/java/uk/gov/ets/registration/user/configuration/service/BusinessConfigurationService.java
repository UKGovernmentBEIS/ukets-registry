package uk.gov.ets.registration.user.configuration.service;

import java.io.IOException;
import java.util.Map;

public interface BusinessConfigurationService {

    /**
     * This function retrieves all the available values from the application properties file.
     *
     * @return {@link Map<String, String>}
     * @throws IOException
     */
    Map<String, String> getApplicationProperties() throws IOException;

    /**
     * This function retrieves the value from the application properties file.
     *
     * @return {@link Map<String, String>}
     * @throws IOException
     */
    Map<String, String> getApplicationPropertyByKey(String key) throws IOException;
}
