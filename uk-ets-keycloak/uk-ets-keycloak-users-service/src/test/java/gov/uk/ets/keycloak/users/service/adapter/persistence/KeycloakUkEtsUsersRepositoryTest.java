package gov.uk.ets.keycloak.users.service.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.querydsl.core.types.Order;
import gov.uk.ets.keycloak.users.service.adapter.persistence.KeycloakUkEtsUsersRepository.Sort;
import gov.uk.ets.keycloak.users.service.application.domain.UserFilter;
import gov.uk.ets.keycloak.users.service.application.domain.UserProjection;
import gov.uk.ets.keycloak.users.service.infrastructure.Constants;
import gov.uk.ets.keycloak.users.service.infrastructure.UserStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import lombok.Builder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.keycloak.models.jpa.entities.ClientEntity;
import org.keycloak.models.jpa.entities.RealmEntity;
import org.keycloak.models.jpa.entities.RoleEntity;
import org.keycloak.models.jpa.entities.UserAttributeEntity;
import org.keycloak.models.jpa.entities.UserEntity;
import org.keycloak.models.jpa.entities.UserRoleMappingEntity;

public class KeycloakUkEtsUsersRepositoryTest {
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private ClientEntity clientEntity;
    private KeycloakUkEtsUsersRepository repository;
    private AddDataCommand command;
    private RepositoryHelper helper;

    @BeforeAll
    public static void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("uk-ets");
    }

    @BeforeEach
    public void setup() {
        helper = new RepositoryHelper();
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        RealmEntity realmEntity = new RealmEntity();
        realmEntity.setId(RepositoryHelper.UK_ETS_REALM_ENTITY_ID);
        realmEntity.setName(Constants.UK_ETS);
        entityManager.persist(realmEntity);
        clientEntity = new ClientEntity();
        clientEntity.setId(Constants.UK_ETS_REGISTRY_API);
        clientEntity.setClientId(Constants.UK_ETS_REGISTRY_API);
        entityManager.persist(clientEntity);
        command = AddDataCommand.builder()
            .email("test@test.com")
            .firstName("Senior")
            .lastName("Administrator")
            .alsoKnownAs("superuser")
            .userId(UUID.randomUUID().toString())
            .role(List.of(Constants.SENIOR_REGISTRY_ADMINISTRATOR))
            .status(UserStatus.ENROLLED.name())
            .build();
        addData(command);
        repository = new KeycloakUkEtsUsersRepository(entityManager);
    }

    @AfterEach
    public void close() {
        entityManager.getTransaction().commit();
        deleteData();
        entityManager.clear();
        entityManager.close();
    }

    @Test
    @DisplayName("Test search by user email")
    public void searchByUserEmail() {
        assertEquals(1, repository
            .fetchUsers(UserFilter.builder()
                .email(command.email)
                .build()).getTotalResults());

        assertEquals(0, repository
            .fetchUsers(UserFilter.builder()
                .email(command.email + "not-exists")
                .build()).getTotalResults());
    }

    @Test
    @DisplayName("Test search by user id or name")
    public void searchByUserIdOrName() {
        assertEquals(1, repository.fetchUsers(UserFilter.builder()
            .nameOrUserId(command.alsoKnownAs.substring(2, 6))
            .build()).getTotalResults());

        assertEquals(0, repository.fetchUsers(UserFilter.builder()
            .nameOrUserId(command.alsoKnownAs + "not exists")
            .build()).getTotalResults());

        assertEquals(1, repository.fetchUsers(UserFilter.builder()
            .nameOrUserId(command.userId.substring(2, 6))
            .build()).getTotalResults());

        assertEquals(0, repository.fetchUsers(UserFilter.builder()
            .nameOrUserId(command.userId + "not exists")
            .build()).getTotalResults());

        assertEquals(1, repository.fetchUsers(UserFilter.builder()
            .nameOrUserId(command.firstName)
            .build()).getTotalResults());

        assertEquals(1, repository.fetchUsers(UserFilter.builder()
            .nameOrUserId(command.lastName)
            .build()).getTotalResults());

        assertEquals(1, repository.fetchUsers(UserFilter.builder()
            .nameOrUserId(command.firstName + " " + command.lastName)
            .build()).getTotalResults());

        assertEquals(1, repository.fetchUsers(UserFilter.builder()
            .nameOrUserId(command.lastName + " " + command.firstName)
            .build()).getTotalResults());
    }

    @Test
    @DisplayName("Test search by user status/state")
    public void searchByUserStatus() {
        assertEquals(1, repository.fetchUsers(UserFilter.builder()
            .statuses(List.of(UserStatus.ENROLLED.name()))
            .build()).getTotalResults());

        assertEquals(0, repository.fetchUsers(UserFilter.builder()
            .statuses(List.of("DUMMY"))
            .build())
            .getTotalResults());
    }

    @Test
    @DisplayName("Test search by role")
    public void searchByRole() {
        assertEquals(1,
            repository.fetchUsers(UserFilter.builder().roles(List.of(Constants.SENIOR_REGISTRY_ADMINISTRATOR)).build())
                .getTotalResults());

        assertEquals(0,
            repository.fetchUsers(UserFilter.builder().roles(List.of(Constants.SENIOR_REGISTRY_ADMINISTRATOR + "unexisted")).build())
                .getTotalResults());
    }

    @ParameterizedTest
    @ValueSource(strings = {"userId", "firstName", "lastName", "status"})
    @DisplayName("Test the sorting of results")
    public void sort(String sortParam) {
        // given
        Map<String, Comparator<UserProjection>> map = new HashMap<>();
        map.put("userId", Comparator.comparing(UserProjection::getUserId));
        map.put("firstName", Comparator.comparing(UserProjection::getFirstName));
        map.put("lastName", Comparator.comparing(UserProjection::getLastName));
        map.put("status", Comparator.comparing(UserProjection::getStatus));
        for (int i = 1; i < 6; i++) {
            addData(AddDataCommand.builder()
                .email("email" + i)
                .firstName("firstname" + i)
                .lastName("lastName" + i)
                .alsoKnownAs("alsoKnownAs")
                .userId("user-id" + i)
                .status("status" + i)
                .build());
        }

        // when
        List<UserProjection> results = repository.fetchUsers(UserFilter.builder().sortField(sortParam)
            .sortingDirection("ASC")
            .build()).getItems();

        // then
        List<UserProjection> expectedProjections = new ArrayList<>(results);
        Collections.sort(expectedProjections, map.get(sortParam));
        assertEquals(results, expectedProjections);

        // when
        results = repository.fetchUsers(UserFilter.builder().sortField(sortParam)
            .sortingDirection("DESC")
            .build()).getItems();

        // then
        Collections.reverse(expectedProjections);
        assertEquals(results, expectedProjections);
    }

    @Test
    @DisplayName("Test Single row per user in the User List regardless of the assigned roles")
    void testSingleRowPerUserRegardlessAssignedRoles() {
    	String uuid1 = UUID.randomUUID().toString();
    	List<AddDataCommand> commands = List.of(AddDataCommand.builder()
	                .email("user01@test.com")
	                .firstName("User01")
	                .lastName("LastUser01")
	                .alsoKnownAs("u01")
	                .userId(uuid1)
	                .role(List.of(Constants.AUTHORISED_REPRESENTATIVE, Constants.AUTHORITY_USER))
	                .status(UserStatus.ENROLLED.name())
                    .build(),
                AddDataCommand.builder()
	                .email("user02@test.com")
	                .firstName("User02")
	                .lastName("LastUser02")
	                .alsoKnownAs("u02")
	                .userId(UUID.randomUUID().toString())
	                .role(List.of(Constants.AUTHORISED_REPRESENTATIVE))
	                .status(UserStatus.ENROLLED.name())
                    .build()
                );
            commands.forEach(command -> addData(command));
    	
        // when
        List<UserProjection> results = fetchUserList(Sort.LAST_NAME,true);
		assertTrue(results != null && !results.isEmpty());
		assertEquals(1, results.stream().filter(user -> user.getUserId().equals(uuid1)).count());
    }
    
    private  List<UserProjection> fetchUserList(Sort sort, boolean isDefault){
        return repository.fetchUsers(UserFilter.builder()
                         .sortField(sort.key)
                         .sortingDirection(((isDefault)?Order.ASC.name():Order.DESC.name()))
                         .build())
                         .getItems();
    }
    
    @Test
    @DisplayName("Test search for non registered users")
    void fetchNonRegisteredUsersCreatedBefore() {

        List<AddDataCommand> commands = List.of(AddDataCommand.builder()
            .createdTimestamp(LocalDateTime.now().minusHours(3).atZone(ZoneId.systemDefault()).toEpochSecond())
            .lastName("Smith")
            .registrationInProgress("true")
            .build(),
            AddDataCommand.builder()
                .createdTimestamp(LocalDateTime.now().minusHours(24).atZone(ZoneId.systemDefault()).toEpochSecond())
                .lastName("Bond")
                .registrationInProgress("true")
                .build(),
            AddDataCommand.builder()
                .createdTimestamp(LocalDateTime.now().minusHours(48).atZone(ZoneId.systemDefault()).toEpochSecond())
                .lastName("Moore")
                .registrationInProgress("false")
                .build()
            );
        commands.forEach(command -> addData(command));
        List<UserEntity> result = repository.fetchNonRegisteredUsersCreatedBefore(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());

        assertEquals(2,result.size());
        assertEquals("Bond",result.get(0).getLastName());
        assertEquals("Smith",result.get(1).getLastName());
    }

    @AfterAll
    public static void tearDown() {
        entityManagerFactory.close();
    }

    private void addData(AddDataCommand command) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID().toString());
        userEntity.setLastName(command.lastName);
        userEntity.setFirstName(command.firstName);
        userEntity.setEmail(command.email, true);
        userEntity.setRealmId(RepositoryHelper.UK_ETS_REALM_ENTITY_ID);
        userEntity.setCreatedTimestamp(command.createdTimestamp);
        entityManager.persist(userEntity);

        UserAttributeEntity uridAttribute = helper.createAttribute(Constants.URID, command.userId, userEntity);
        entityManager.persist(uridAttribute);

        UserAttributeEntity registrationInProgressAttribute = helper.createAttribute(Constants.REGISTRATION_IN_PROGRESS, command.registrationInProgress, userEntity);
        entityManager.persist(registrationInProgressAttribute);
        
        UserAttributeEntity alsoKnownAsAttribute = helper.createAttribute(Constants.ALSO_KNOWN_AS, command.alsoKnownAs, userEntity);
        entityManager.persist(alsoKnownAsAttribute);
        
        if (command.role != null && !command.nonUkEtsUser) {
        	for(String uRole: command.role) {
	            RoleEntity roleEntity = entityManager.find(RoleEntity.class, uRole);
	            if (roleEntity == null) {
	                roleEntity = new RoleEntity();
	                roleEntity.setId(uRole);
	                roleEntity.setClientId(clientEntity.getId());
	                roleEntity.setName(uRole);
	                entityManager.persist(roleEntity);
	            }
	            UserRoleMappingEntity userRoleMappingEntity = new UserRoleMappingEntity();
	            userRoleMappingEntity.setUser(userEntity);
	            userRoleMappingEntity.setRoleId(roleEntity.getId());
	            entityManager.persist(userRoleMappingEntity);
        	}
        }
        if (command.status != null) {
            UserAttributeEntity stateAttribute = helper.createAttribute(Constants.STATE, command.status, userEntity);
            entityManager.persist(stateAttribute);
        }
    }

    private void deleteData() {
        entityManager.getTransaction().begin();
        entityManager.createQuery("delete from UserRoleMappingEntity ur").executeUpdate();
        entityManager.createQuery("delete from UserAttributeEntity ua").executeUpdate();
        entityManager.createQuery("delete from RoleEntity r").executeUpdate();
        entityManager.createQuery("delete from UserEntity u").executeUpdate();
        entityManager.createQuery("delete from ClientEntity c").executeUpdate();
        entityManager.createQuery("delete from RealmEntity r").executeUpdate();
        entityManager.getTransaction().commit();
    }

    @Builder
    private static class AddDataCommand {
        private String userId;
        private String firstName;
        private String lastName;
        private String alsoKnownAs;
        private String email;
        private boolean nonUkEtsUser;
        private List<String> role;
        private String status;
        private String lastSignInFrom;
        private String lastSignInTo;
        private String registeredOn;
        private Long createdTimestamp;
        private String registrationInProgress;  
    }
}