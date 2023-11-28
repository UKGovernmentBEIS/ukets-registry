package gov.uk.ets.keycloak.users.service.adapter.persistence;

import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.keycloak.users.service.application.domain.QUserPersonalInfo;
import gov.uk.ets.keycloak.users.service.application.domain.UserPersonalInfo;
import gov.uk.ets.keycloak.users.service.application.domain.UserPersonalInfoRepository;
import gov.uk.ets.keycloak.users.service.infrastructure.Constants;
import java.util.List;
import javax.persistence.EntityManager;
import org.keycloak.models.jpa.entities.QUserAttributeEntity;
import org.keycloak.models.jpa.entities.QUserEntity;

public class UkEtsUserPersonalInfoRepository implements UserPersonalInfoRepository {
    private static final QUserEntity userEntity = QUserEntity.userEntity;
    private static final QUserAttributeEntity uridAttribute = new QUserAttributeEntity("urid");
    private static final QUserAttributeEntity alsoKnownAsAttribute = new QUserAttributeEntity("alsoKnownAs");
    private static final QUserAttributeEntity workBuildingAndStreetAttribute = new QUserAttributeEntity("workBuildingAndStreet");
    private static final QUserAttributeEntity workBuildingAndStreetOptionalAttribute = new QUserAttributeEntity("workBuildingAndStreetOptional");
    private static final QUserAttributeEntity workBuildingAndStreetOptional2Attribute = new QUserAttributeEntity("workBuildingAndStreetOptional2");
    private static final QUserAttributeEntity workPostCodeAttribute = new QUserAttributeEntity("workPostCode");
    private static final QUserAttributeEntity workTownOrCityAttribute = new QUserAttributeEntity("workTownOrCity");
    private static final QUserAttributeEntity workStateOrProvinceAttribute = new QUserAttributeEntity("workStateOrProvince");
    private static final QUserAttributeEntity workCountryAttribute = new QUserAttributeEntity("workCountry");
    private static final QUserAttributeEntity workCountryCodeAttribute = new QUserAttributeEntity("workCountryCode");
    private static final QUserAttributeEntity workPhoneNumberAttribute = new QUserAttributeEntity("workPhoneNumber");

    private final EntityManager entityManager;
    private final QUserPersonalInfo userPersonalInfo;

    public UkEtsUserPersonalInfoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.userPersonalInfo = new QUserPersonalInfo(
            uridAttribute.value,
            userEntity.firstName,
            userEntity.lastName,
            alsoKnownAsAttribute.value,
            workBuildingAndStreetAttribute.value,
            workBuildingAndStreetOptionalAttribute.value,
            workBuildingAndStreetOptional2Attribute.value,
            workPostCodeAttribute.value,
            workTownOrCityAttribute.value,
            workStateOrProvinceAttribute.value,
            workCountryAttribute.value,
            workCountryCodeAttribute.value,
            workPhoneNumberAttribute.value,
            userEntity.email
        );
    }

    @Override
    public List<UserPersonalInfo> fetchUserPersonalInfos(List<String> urids) {
        return new JPAQuery<UserPersonalInfo>(entityManager).select(userPersonalInfo)
            .from(userEntity)
            .join(userEntity.attributes, uridAttribute)
            .on(uridAttribute.name.eq(Constants.URID).and(uridAttribute.value.in(urids)))
            .leftJoin(userEntity.attributes, alsoKnownAsAttribute)
            .on(alsoKnownAsAttribute.name.eq(Constants.ALSO_KNOWN_AS))
            .leftJoin(userEntity.attributes, workBuildingAndStreetAttribute)
            .on(workBuildingAndStreetAttribute.name.eq(Constants.WORK_BUILDING_AND_STREET))
            .leftJoin(userEntity.attributes, workBuildingAndStreetOptionalAttribute)
            .on(workBuildingAndStreetOptionalAttribute.name.eq(Constants.WORK_BUILDING_AND_STREET_OPTIONAL))
            .leftJoin(userEntity.attributes, workBuildingAndStreetOptional2Attribute)
            .on(workBuildingAndStreetOptional2Attribute.name.eq(Constants.WORK_BUILDING_AND_STREET_OPTIONAL_2))
            .leftJoin(userEntity.attributes, workPostCodeAttribute)
            .on(workPostCodeAttribute.name.eq(Constants.WORK_POST_CODE))
            .leftJoin(userEntity.attributes, workTownOrCityAttribute)
            .on(workTownOrCityAttribute.name.eq(Constants.WORK_TOWN_OR_CITY))
            .leftJoin(userEntity.attributes, workStateOrProvinceAttribute)
            .on(workStateOrProvinceAttribute.name.eq(Constants.WORK_STATE_OR_PROVINCE))
            .leftJoin(userEntity.attributes, workCountryAttribute)
            .on(workCountryAttribute.name.eq(Constants.WORK_COUNTRY))
            .leftJoin(userEntity.attributes, workCountryCodeAttribute)
            .on(workCountryCodeAttribute.name.eq(Constants.WORK_COUNTRY_CODE))
            .leftJoin(userEntity.attributes, workPhoneNumberAttribute)
            .on(workPhoneNumberAttribute.name.eq(Constants.WORK_PHONE_NUMBER))
            .distinct().fetch();
    }
}
