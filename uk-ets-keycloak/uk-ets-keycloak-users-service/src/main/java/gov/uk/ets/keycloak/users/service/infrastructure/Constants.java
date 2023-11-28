package gov.uk.ets.keycloak.users.service.infrastructure;

/**
 * Keycloak extension constants
 */
public class Constants {

    private Constants() {
    }

    /**
     * The uk-ets realm id
     */
    public static final String UK_ETS = "uk-ets";
    /**
     * The name of Last sign in user attribute
     */
    public static final String LAST_SIGN_IN_DATE = "lastLoginDate";
    /**
     * The name of user registration date attribute
     */
    public static final String REGISTERED_ON_DATE="registeredOnDate";
    /**
     * The also known as user attribute
     */
    public static final String ALSO_KNOWN_AS = "alsoKnownAs";
    /**
     * The id of custom required action provider
     */
    public static final String LAST_LOGIN_DATE_RECORDER = "last-login-date-recorder";
    /**
     * The display text of custom required action provider
     */
    public static final String LAST_LOGIN_DATE_RECORDER_DISPLAY_TEXT = "UK ETS User Last login date recorder";
    /**
     * The uk ets registry keycloak client
     */
    public static final String UK_ETS_REGISTRY_API = "uk-ets-registry-api";
    /**
     * The name of user status/status attribute
     */
    public static final String STATE = "state";
    /**
     * The name of urid user attribute
     */
    public static final String URID = "urid";    
    /**
     * The name of registration_in_progress user attribute
     */
    public static final String REGISTRATION_IN_PROGRESS = "registration_in_progress";
    /**
     * The maximum number of results in page
     */
    public static final Long MAX_NUMBER_OF_RESULTS = 1000L;
    /**
     * The default page of results
     */
    public static final int FIRST_PAGE = 0;
    /**
     * The junior registry administrator keycloak role
     */
    public static final String JUNIOR_REGISTRY_ADMINISTRATOR = "junior-registry-administrator";
    /**
     * The readonly registry administrator keycloak role
     */
    public static final String READONLY_REGISTRY_ADMINISTRATOR = "readonly-administrator";
    /**
     * The authority user keycloak role
     */
    public static final String AUTHORITY_USER = "authority-user";
    /**
     * The authorised representative keycloak role
     */
    public static final String AUTHORISED_REPRESENTATIVE = "authorized-representative";
    /**
     * The system administrator keycloak role
     */
    public static final String SYSTEM_ADMINISTRATOR = "system-administrator";
    /**
     * The senior registry administrator keycloak role
     */
    public static final String SENIOR_REGISTRY_ADMINISTRATOR = "senior-registry-administrator";
    /**
     * The work address
     */
    public static final String WORK_BUILDING_AND_STREET = "workBuildingAndStreet";
    /**
     * The optional second work address
     */
    public static final String WORK_BUILDING_AND_STREET_OPTIONAL = "workBuildingAndStreetOptional";
    /**
     * The optional third work address
     */
    public static final String WORK_BUILDING_AND_STREET_OPTIONAL_2 = "workBuildingAndStreetOptional2";
    /**
     * The postal code of work address
     */
    public static final String WORK_POST_CODE = "workPostCode";
    /**
     * The town or city of the work address
     */
    public static final String WORK_TOWN_OR_CITY = "workTownOrCity";
    /**
     * The state or province of the work address
     */
    public static final String WORK_STATE_OR_PROVINCE = "workStateOrProvince";
    /**
     * The country of the work address
     */
    public static final String WORK_COUNTRY = "workCountry";
    /**
     * The country code of the work address
     */
    public static final String WORK_COUNTRY_CODE = "workCountryCode";
    /**
     * The work phone number
     */
    public static final String WORK_PHONE_NUMBER = "workPhoneNumber";
    /**
     * The work email address
     */
    public static final String WORK_EMAIL_ADDRESS = "workEmailAddress";
}
