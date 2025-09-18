package gov.uk.ets.registry.api.user.migration;

import static gov.uk.ets.registry.api.migration.domain.MigratorName.WORK_MOBILE_PHONE_ATTRIBUTE_MIGRATOR;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import jakarta.ws.rs.ClientErrorException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Log4j2
public class WorkMobilePhoneAttributeMigrator implements Migrator {

    private static final String KEYCLOAK_ATTRIBUTE_WORK_COUNTRY_CODE = "workCountryCode";
    private static final String KEYCLOAK_ATTRIBUTE_WORK_PHONE_NUMBER = "workPhoneNumber";
    private static final String KEYCLOAK_ATTRIBUTE_WORK_MOBILE_COUNTRY_CODE = "workMobileCountryCode";
    private static final String KEYCLOAK_ATTRIBUTE_WORK_MOBILE_PHONE_NUMBER = "workMobilePhoneNumber";
    private static final String KEYCLOAK_ATTRIBUTE_WORK_ALTERNATIVE_COUNTRY_CODE = "workAlternativeCountryCode";
    private static final String KEYCLOAK_ATTRIBUTE_WORK_ALTERNATIVE_PHONE_NUMBER = "workAlternativePhoneNumber";
    private static final String KEYCLOAK_ATTRIBUTE_NO_MOBILE_PHONE_NUMBER_REASON = "noMobilePhoneNumberReason";
    private static final String NO_MOBILE_PHONE_NUMBER_REASON_DEFAULT_VALUE = "Not entered during user registration.";


    private final MigratorHistoryRepository migratorHistoryRepository;
    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    private final UserRepository userRepository;

    @Transactional
    public void migrate() {

        log.info("Starting migration of the user attribute work mobile phone number...");

        List<MigratorHistory> migratorHistoryList = migratorHistoryRepository.findByMigratorName(WORK_MOBILE_PHONE_ATTRIBUTE_MIGRATOR);
        if (CollectionUtils.isNotEmpty(migratorHistoryList)) {
            log.info("[Work Mobile Phone Attribute Migrator], has already been performed, skipping.");
            return;
        }

        userRepository.findAll()
            .stream()
            .filter(user -> user.getState() != UserStatus.DEACTIVATED)
            .map(this::getKeycloakUser)
            .filter(Objects::nonNull)
            .forEach(this::updateUserMobilePhone);

        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(WORK_MOBILE_PHONE_ATTRIBUTE_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);

        log.info("Migration of Work Mobile Phone Number attribute completed");
    }

    private UserRepresentation getKeycloakUser(User user) {
        try {
            return serviceAccountAuthorizationService.getUser(user.getIamIdentifier());
        } catch (ClientErrorException e) {
            log.warn("Could not retrieve user from keycloak with urid: {} ", user.getUrid());
        }
        return null;
    }

    private boolean hasMobilePhoneAsWorkPhone(List<String> workPhoneCode, List<String> workPhoneNumber) {

        String code = Optional.ofNullable(workPhoneCode)
            .stream()
            .flatMap(Collection::stream)
            .filter(Objects::nonNull)
            .map(PhoneNumberUtil::normalizeDigitsOnly)
            .findFirst()
            .orElse("");

        String phone = Optional.ofNullable(workPhoneNumber)
            .stream()
            .flatMap(Collection::stream)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("");

        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber number = phoneUtil.parse("+" + code + phone, "");
            if (!phoneUtil.isValidNumber(number)) {
                return false;
            }
            if (phoneUtil.getNumberType(number) == PhoneNumberUtil.PhoneNumberType.MOBILE) {
                return true;
            }
        } catch (NumberParseException e) {
            log.warn("Invalid Phone Number {} {}", code, phone);
        }
        return false;
    }

    private void updateUserMobilePhone(UserRepresentation userRepresentation) {

        Map<String, List<String>> attributes = userRepresentation.getAttributes();

        List<String> workPhoneCode = attributes.get(KEYCLOAK_ATTRIBUTE_WORK_COUNTRY_CODE);
        List<String> workPhoneNumber = attributes.get(KEYCLOAK_ATTRIBUTE_WORK_PHONE_NUMBER);

        if (hasMobilePhoneAsWorkPhone(workPhoneCode, workPhoneNumber)) {
            attributes.putIfAbsent(KEYCLOAK_ATTRIBUTE_WORK_MOBILE_COUNTRY_CODE, workPhoneCode);
            attributes.putIfAbsent(KEYCLOAK_ATTRIBUTE_WORK_MOBILE_PHONE_NUMBER, workPhoneNumber);
        } else {
            attributes.putIfAbsent(KEYCLOAK_ATTRIBUTE_NO_MOBILE_PHONE_NUMBER_REASON, List.of(NO_MOBILE_PHONE_NUMBER_REASON_DEFAULT_VALUE));
            attributes.putIfAbsent(KEYCLOAK_ATTRIBUTE_WORK_ALTERNATIVE_COUNTRY_CODE, workPhoneCode);
            attributes.putIfAbsent(KEYCLOAK_ATTRIBUTE_WORK_ALTERNATIVE_PHONE_NUMBER, workPhoneNumber);
        }

        serviceAccountAuthorizationService.updateUserDetails(userRepresentation);
    }
}
