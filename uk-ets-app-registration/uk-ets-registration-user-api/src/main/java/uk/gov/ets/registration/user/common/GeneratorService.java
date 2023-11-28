package uk.gov.ets.registration.user.common;

/**
 * Service for generating random artifacts (e.g. URIDs, enrolment keys etc.)
 */
public interface GeneratorService {

    /**
     * Produces a random user id (URID).
     * @return a URID.
     */
    String generateURID();

    /**
     * Validates the provided URID.
     * @param urid The user id.
     * @return false/true
     */
    boolean validateURID(String urid);

}
