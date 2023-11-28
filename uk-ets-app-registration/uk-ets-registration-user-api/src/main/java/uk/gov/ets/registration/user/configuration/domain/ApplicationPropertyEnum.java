package uk.gov.ets.registration.user.configuration.domain;

import lombok.Getter;

/**
 * In this enumeration we declare which application properties are available to the user through the application.
 * All the properties are declared below and are available.
 * <p>
 * Please remove the properties that must not be displayed.
 */
@Getter
public enum ApplicationPropertyEnum {

    /**
     * The email address where to send, when any problems concerning registration arouse
     */
    REGISTRATION_KEYCLOAK_EMAIL_FROM("user.registration.keycloak.emailFrom"),
    /**
     * The subject of the above email, when any problems concerning registration arouse
     */
    REGISTRATION_KEYCLOAK_EMAIL_SUBJECT_FROM("user.registration.keycloak.emailSubject"),
    /**
     * The site where to communicate with service desk
     */
    REGISTRY_SERVICE_DESK_WEB("registry.service.desk.web");

    private String key;

    ApplicationPropertyEnum(String key) {
        this.key = key;
    }
}
