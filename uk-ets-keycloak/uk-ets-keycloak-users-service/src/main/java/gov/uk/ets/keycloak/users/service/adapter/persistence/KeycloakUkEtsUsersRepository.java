package gov.uk.ets.keycloak.users.service.adapter.persistence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.persistence.EntityManager;

import org.keycloak.models.jpa.entities.QClientEntity;
import org.keycloak.models.jpa.entities.QRealmEntity;
import org.keycloak.models.jpa.entities.QRoleEntity;
import org.keycloak.models.jpa.entities.QUserAttributeEntity;
import org.keycloak.models.jpa.entities.QUserEntity;
import org.keycloak.models.jpa.entities.QUserRoleMappingEntity;
import org.keycloak.models.jpa.entities.UserEntity;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;

import gov.uk.ets.keycloak.users.service.application.domain.Pageable;
import gov.uk.ets.keycloak.users.service.application.domain.QUserProjection;
import gov.uk.ets.keycloak.users.service.application.domain.UkEtsUsersRepository;
import gov.uk.ets.keycloak.users.service.application.domain.UserFilter;
import gov.uk.ets.keycloak.users.service.application.domain.UserProjection;
import gov.uk.ets.keycloak.users.service.infrastructure.Constants;
import gov.uk.ets.keycloak.users.service.infrastructure.OptionalBooleanBuilder;

/**
 * The {@link UkEtsUsersRepository} outbound adapter.
 */
public class KeycloakUkEtsUsersRepository implements UkEtsUsersRepository {
    private static final QUserEntity userEntity = QUserEntity.userEntity;
    private static final QRoleEntity roleEntity = QRoleEntity.roleEntity;
    private static final QRoleEntity clientRoleEntity = new QRoleEntity("clientRole");
    private static final QUserRoleMappingEntity userRoleMappingEntity = QUserRoleMappingEntity.userRoleMappingEntity;
    private static final QUserAttributeEntity userStateAttribute = new QUserAttributeEntity("userState");
    private static final QUserAttributeEntity uridAttribute = new QUserAttributeEntity("urid");
    private static final QUserAttributeEntity lastSignInDateAttribute = new QUserAttributeEntity("lastSignInDate");
    private static final QUserAttributeEntity registeredOnDateAttribute = new QUserAttributeEntity("registeredOnDate");
    private static final QUserAttributeEntity alsoKnownAsAttribute = new QUserAttributeEntity("alsoKnownAs");
    private static final QUserAttributeEntity registrationInProgressAttribute = new QUserAttributeEntity("registration_in_progress");
    private static final QClientEntity clientEntity = new QClientEntity("registryApiClient");
    private static final QRealmEntity realmEntity = QRealmEntity.realmEntity;

    private final EntityManager entityManager;
    private final QUserProjection userProjection;

    public KeycloakUkEtsUsersRepository(EntityManager entityManager) {
        this.entityManager = entityManager;

        this.userProjection = new QUserProjection(
            uridAttribute.value.as(Sort.USER_ID.key),
            userEntity.firstName.as(Sort.FIRST_NAME.key),
            userEntity.lastName.as(Sort.LAST_NAME.key),
            userStateAttribute.value.as(Sort.STATUS.key),
            lastSignInDateAttribute.value.as(Sort.LAST_SIGN_IN.key),
            registeredOnDateAttribute.value.as(Sort.REGISTERED_ON.key),
        	alsoKnownAsAttribute.value.as(Sort.KNOWN_AS.key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserEntity> fetchNonRegisteredUsersCreatedBefore(Long beforeDateTime) {
        return new JPAQuery<UserProjection>(entityManager)
            .select(userEntity)
            .from(userEntity)
            .join(userEntity.attributes, registrationInProgressAttribute)
            .on(registrationInProgressAttribute.name.eq("registration_in_progress"))
            .where(new OptionalBooleanBuilder(userEntity.realmId
                .in(JPAExpressions.select(realmEntity.id).from(realmEntity)
                    .where(realmEntity.name.eq(Constants.UK_ETS))))
                .notNullAnd(registrationInProgressAttribute.value::equalsIgnoreCase, Boolean.TRUE.toString())
                .notNullAnd(userEntity.createdTimestamp::lt, beforeDateTime)
                .build())
            .orderBy(userEntity.createdTimestamp.asc())
            .distinct()
            .fetch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable fetchUsers(UserFilter filter) {
        JPAQuery<UserProjection> query = getSearchQuery(filter);
        Sort sort = Sort.getSort(filter.getSortField());
        Long pageSize = Optional.ofNullable(filter.getPageSize()).orElse(Constants.MAX_NUMBER_OF_RESULTS);
        Integer page = Optional.ofNullable(filter.getPage()).orElse(Constants.FIRST_PAGE);
        return Pageable.builder().items(query
            .orderBy(sort.getOrderSpecifier(filter.getSortingDirection()))
            .limit(pageSize)
            .offset(pageSize * page)
            .fetch())
            .totalResults(query.fetchCount())
            .build();
    }

    private JPAQuery<UserProjection> getSearchQuery(UserFilter filter) {
        return new JPAQuery<UserProjection>(entityManager)
                .select(userProjection)
                .from(userEntity)
                 // inner join of user_entity with user_attribute for the urid
                .join(userEntity.attributes, uridAttribute)
                .on(uridAttribute.name.eq(Constants.URID))
                // inner join of user_entity with user_attribute for the state
                .join(userEntity.attributes, userStateAttribute)
                .on(userStateAttribute.name.eq(Constants.STATE))
                // left join of user_entity with user_attribute for the alsoKnownAs
                .leftJoin(userEntity.attributes, alsoKnownAsAttribute)
                .on(alsoKnownAsAttribute.name.eq(Constants.ALSO_KNOWN_AS))
                // left join of user_entity with user_attribute for the lastLoginDate
                .leftJoin(userEntity.attributes, lastSignInDateAttribute)
                .on(lastSignInDateAttribute.name.eq(Constants.LAST_SIGN_IN_DATE))
                // left join of user_entity with user_attribute for the registeredOnDate
                .leftJoin(userEntity.attributes, registeredOnDateAttribute)
                .on(registeredOnDateAttribute.name.eq(Constants.REGISTERED_ON_DATE))
                // left join of keycloak_role with user_role_mapping
                .leftJoin(roleEntity).on(roleEntity.id.in(
                        JPAExpressions.select(userRoleMappingEntity.roleId)
                                        .from(userRoleMappingEntity)
                                        .where(userRoleMappingEntity.user.id.eq(userEntity.id))
                )
                .and(roleEntity.id.in(
                        // selects keycloak_role's ids for the inner client
                        JPAExpressions.select(clientRoleEntity.id)
                                .from(clientRoleEntity)
                                .where(clientRoleEntity.clientId.eq(
                                        // selects client's id from client when client's client_id = uk-ets-registry-api
                                        JPAExpressions.select(clientEntity.id)
                                                .from(clientEntity)
                                                .where(clientEntity.clientId.eq(Constants.UK_ETS_REGISTRY_API))))
                )))
                .where(new OptionalBooleanBuilder(userEntity.realmId
                        .in(   // selects realm's id from realm when realm's name = uk-ets
                                JPAExpressions.select(realmEntity.id)
                                .from(realmEntity)
                                .where(realmEntity.name.eq(Constants.UK_ETS))
                        ))
                        .notNullAnd(this::getNameOrUserIdExpression, filter.getNameOrUserId())
                        .notNullAnd(userStateAttribute.value::in, filter.getStatuses())
                        .notNullAnd(userEntity.email::containsIgnoreCase, filter.getEmail())
                        .notNullAnd(this::getLastSignInFromExpression, filter.getLastSignInFrom())
                        .notNullAnd(this::getLastSignInToExpression, filter.getLastSignInTo())
                        .notNullAnd(roleEntity.name::in, filter.getRoles())
                        .notNullAnd(this::getNotInRolesExpression, filter.getExcludedRoles())
                        .build())
                .distinct();
    }

    private BooleanExpression getNotInRolesExpression(List<String> excludedRoles) {
        return roleEntity.name.isNull();
    }

    private BooleanExpression getLastSignInFromExpression(LocalDateTime lastSignInFrom) {
        String lastSignInFromStr = lastSignInFrom.format(DateTimeFormatter.ISO_DATE_TIME);
        return lastSignInDateAttribute.value.eq(lastSignInFromStr)
            .or(lastSignInDateAttribute.value.gt(lastSignInFromStr));
    }

    private BooleanExpression getLastSignInToExpression(LocalDateTime lastSignInTo) {
        return lastSignInDateAttribute.value.lt(lastSignInTo.format(DateTimeFormatter.ISO_DATE_TIME));
    }

    private BooleanExpression getNameOrUserIdExpression(String nameOrUserId) {
        BooleanExpression userIdPredicate = uridAttribute.value.containsIgnoreCase(nameOrUserId);
        BooleanExpression alsoKnownAsPredicate = alsoKnownAsAttribute.value.containsIgnoreCase(nameOrUserId);
        BooleanExpression lastNameFirstNamePredicate = userEntity.lastName.append(" ").concat(userEntity.firstName)
            .containsIgnoreCase(nameOrUserId);
        BooleanExpression firstNameLastNamePredicate = userEntity.firstName.append(" ").concat(userEntity.lastName)
            .containsIgnoreCase(nameOrUserId);
        return userIdPredicate.or(firstNameLastNamePredicate).or(lastNameFirstNamePredicate).or(alsoKnownAsPredicate);
    }

    /**
     * Sort data enumeration
     */
    enum Sort {
        USER_ID("userId"),
        FIRST_NAME("firstName"),
        LAST_NAME("lastName"),
        LAST_SIGN_IN("lastSignIn"),
        REGISTERED_ON("registeredOn"),
        STATUS("status"),
    	KNOWN_AS("knownAs");

        String key;

        Sort(String key) {
            this.key = key;
        }

        public OrderSpecifier<?> getOrderSpecifier(String order) {
            ComparableExpressionBase<? extends Comparable> expressionBase = Expressions.stringPath(key);
            return order != null && Order.ASC.name().equals(order.toUpperCase()) ?
                expressionBase.asc() : expressionBase.desc();
        }

        public static Sort getSort(String key) {
            return Stream.of(Sort.values())
                .filter(sort -> sort.key.equals(key))
                .findFirst()
                .orElse(Sort.REGISTERED_ON);
        }
    }

    /**
     * Role label enum
     */
    enum RoleLabel {
        USER,
        SENIOR_ADMIN,
        JUNIOR_ADMIN,
        READONLY_ADMIN,
        SYSTEM_ADMINISTRATOR,
        AUTHORITY_USER,
        AUTHORISED_REPRESENTATIVE;
    }

}
