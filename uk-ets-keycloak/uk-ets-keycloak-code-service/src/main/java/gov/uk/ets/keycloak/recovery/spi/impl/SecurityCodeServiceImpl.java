package gov.uk.ets.keycloak.recovery.spi.impl;

import gov.uk.ets.keycloak.recovery.dto.SecurityCodeDTO;
import gov.uk.ets.keycloak.recovery.jpa.SecurityCode;
import gov.uk.ets.keycloak.recovery.spi.SecurityCodeService;
import jakarta.persistence.EntityManager;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SecurityCodeServiceImpl implements SecurityCodeService {

    private static final String QUERY_USER_SECURITY_CODES =
        "SELECT sc FROM security_code sc WHERE sc.userId = :userId ORDER BY sc.expiredAt DESC";
    private static final String QUERY_USER_CURRENT_CODE =
        "SELECT sc FROM security_code sc WHERE sc.userId = :userId and sc.valid = :valid ORDER BY sc.expiredAt DESC";
    private static final String DELETE_QUERY = "DELETE FROM security_code WHERE userId = :userId";
    private static final int LENGTH = 6;

    private static final long TIME_TO_LIVE_MILLIS = 300000;
    public static final long TIMER_BUFFER_MILLIS = 6000; // 2% of 300000

    private final EntityManager entityManager;

    public SecurityCodeServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<SecurityCodeDTO> findSecurityCodesByUserId(String userId) {
        return getSecurityCodes(userId)
            .stream().map(this::mapToSecurityCodeDTO)
            .collect(Collectors.toList());
    }

    private List<SecurityCode> getSecurityCodes(String userId) {
        return entityManager.createQuery(QUERY_USER_SECURITY_CODES, SecurityCode.class)
            .setParameter("userId", userId)
            .getResultList();
    }

    @Override
    public SecurityCodeDTO requestSecurityCode(String userId, String email) {
        return requestSecurityCode(userId, email, null, null);
    }

    @Override
    public SecurityCodeDTO requestSecurityCode(String userId, String countryCode, String phoneNumber) {
        return requestSecurityCode(userId, null, countryCode, phoneNumber);
    }

    @Override
    public SecurityCodeDTO addAttempt(String userId) {
        SecurityCode securityCode = entityManager.createQuery(QUERY_USER_CURRENT_CODE, SecurityCode.class)
            .setParameter("userId", userId)
            .setParameter("valid", true)
            .getSingleResult();

        int increasedAttempts = Optional.ofNullable(securityCode.getAttempts()).orElse(0) + 1;
        securityCode.setAttempts(increasedAttempts);
        entityManager.persist(securityCode);

        return mapToSecurityCodeDTO(securityCode);
    }

    private SecurityCodeDTO requestSecurityCode(String userId, String email, String countryCode, String phoneNumber) {

        getSecurityCodes(userId).forEach(code -> code.setValid(false));

        SecurityCode securityCode = new SecurityCode();

        securityCode.setUserId(userId);
        securityCode.setCode(generateSecurityCode());
        securityCode.setValid(true);
        securityCode.setLoggedIn(false);
        securityCode.setEmail(email);
        securityCode.setCountryCode(countryCode);
        securityCode.setPhoneNumber(phoneNumber);

        Instant now = Instant.now();
        securityCode.setCreatedAt(Date.from(now));
        securityCode.setExpiredAt(Date.from(now.plusMillis(TIME_TO_LIVE_MILLIS + TIMER_BUFFER_MILLIS)));

        entityManager.persist(securityCode);
        return mapToSecurityCodeDTO(securityCode);
    }

    private String generateSecurityCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            sb.append(random.nextInt(10)); // Generates a random digit (0-9)
        }
        return sb.toString();
    }

    @Override
    public void clearSecurityCodes(String userId) {
        entityManager.createQuery(DELETE_QUERY)
            .setParameter("userId", userId)
            .executeUpdate();
    }

    @Override
    public void close() {
        // nothing here
    }

    private SecurityCodeDTO mapToSecurityCodeDTO(SecurityCode securityCode) {
        SecurityCodeDTO dto = new SecurityCodeDTO();
        dto.setId(securityCode.getId());
        dto.setUserId(securityCode.getUserId());
        dto.setEmail(securityCode.getEmail());
        dto.setCountryCode(securityCode.getCountryCode());
        dto.setPhoneNumber(securityCode.getPhoneNumber());
        dto.setCode(securityCode.getCode());
        dto.setAttempts(securityCode.getAttempts());
        dto.setValid(securityCode.getValid());
        dto.setLoggedIn(securityCode.getLoggedIn());
        dto.setExpiresInMillis(calculateRemainingMillis(securityCode.getExpiredAt()));
        dto.setCreatedAt(securityCode.getCreatedAt());
        return dto;
    }

    private Long calculateRemainingMillis(Date expiredAt) {
        return expiredAt == null ? null : Math.max(((expiredAt.getTime() - System.currentTimeMillis()) - TIMER_BUFFER_MILLIS), 0);
    }
}
