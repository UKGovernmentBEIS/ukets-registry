package gov.uk.ets.keycloak.users.service.adapter.persistence;


import gov.uk.ets.keycloak.users.service.application.domain.UserPersonalInfo;
import gov.uk.ets.keycloak.users.service.infrastructure.Constants;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.Builder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.keycloak.models.jpa.entities.UserEntity;

class UkEtsUserPersonalInfoRepositoryTest {
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private UkEtsUserPersonalInfoRepository repository;
    private RepositoryHelper helper = new RepositoryHelper();

    @BeforeAll
    public static void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("uk-ets");
    }

    @BeforeEach
    public void setup() {
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        repository = new UkEtsUserPersonalInfoRepository(entityManager);
    }

    @AfterEach
    public void close() {
        entityManager.getTransaction().commit();
        deleteData();
        entityManager.clear();
        entityManager.close();
    }

    private void deleteData() {
        entityManager.getTransaction().begin();
        entityManager.createQuery("delete from UserAttributeEntity ua").executeUpdate();
        entityManager.createQuery("delete from UserEntity u").executeUpdate();
        entityManager.getTransaction().commit();
    }

    @Test
    void fetchUserPersonalInfos() {

        List<AddDataCommand> commands = List.of(AddDataCommand.builder()
            .urid("UK12324344")
            .firstName("John")
            .lastName("Alerton")
            .workBuildingAndStreet("a workBuildingAndStreet")
            .workBuildingAndStreetOptional("a workBuildingAndStreet optional")
            .workBuildingAndStreetOptional2("a workBuildingAndStreet optional 2")
            .workCountry("a work country")
            .build(),
            AddDataCommand.builder()
                .urid("UK12324346")
                .firstName("John")
                .lastName("Alerton")
                .workBuildingAndStreet("a workBuildingAndStreet")
                .workBuildingAndStreetOptional("a workBuildingAndStreet optional")
                .workBuildingAndStreetOptional2("a workBuildingAndStreet optional 2")
                .workCountry("a work country")
                .build(),
            AddDataCommand.builder()
                .urid("UK12324349")
                .firstName("John")
                .lastName("Alerton")
                .workBuildingAndStreet("a workBuildingAndStreet")
                .workBuildingAndStreetOptional("a workBuildingAndStreet optional")
                .workBuildingAndStreetOptional2("a workBuildingAndStreet optional 2")
                .workCountry("a work country")
                .workStateOrProvince("a work state")
                .build()
            );
        commands.forEach(command -> addData(command));
        List<UserPersonalInfo> result = repository.fetchUserPersonalInfos(commands.stream().map(command -> command.urid).collect(
            Collectors.toList()));

        assertEquals(commands.size(), result.size());
    }

    private void addData(AddDataCommand command) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID().toString());
        userEntity.setLastName(command.lastName);
        userEntity.setFirstName(command.firstName);
        userEntity.setRealmId(Constants.UK_ETS);
        entityManager.persist(userEntity);
        addAttribute(Constants.URID, command.urid, userEntity);
        addAttribute(Constants.WORK_BUILDING_AND_STREET, command.workBuildingAndStreet, userEntity);
        addAttribute(Constants.WORK_BUILDING_AND_STREET_OPTIONAL, command.workBuildingAndStreetOptional, userEntity);
        addAttribute(Constants.WORK_BUILDING_AND_STREET_OPTIONAL_2, command.workBuildingAndStreetOptional2, userEntity);
        addAttribute(Constants.WORK_COUNTRY, command.workCountry, userEntity);
        addAttribute(Constants.WORK_COUNTRY_CODE, command.workCountryCode, userEntity);
        addAttribute(Constants.WORK_EMAIL_ADDRESS, command.workEmailAddress, userEntity);
        addAttribute(Constants.WORK_PHONE_NUMBER, command.workPhoneNumber, userEntity);
        addAttribute(Constants.WORK_POST_CODE, command.workPostCode, userEntity);
        addAttribute(Constants.WORK_TOWN_OR_CITY, command.workTownOrCity, userEntity);
        addAttribute(Constants.WORK_STATE_OR_PROVINCE, command.workStateOrProvince, userEntity);
    }

    private void addAttribute(String attributeName, String value, UserEntity userEntity) {
        if (value == null) {
            return;
        }
        entityManager.persist(helper.createAttribute(attributeName, value, userEntity));
    }

    @Builder
    private static class AddDataCommand {
        private String urid;
        private String firstName;
        private String lastName;
        private String workBuildingAndStreet;
        private String workBuildingAndStreetOptional;
        private String workBuildingAndStreetOptional2;
        private String workPostCode;
        private String workTownOrCity;
        private String workStateOrProvince;
        private String workCountry;
        private String workCountryCode;
        private String workPhoneNumber;
        private String workEmailAddress;
    }
}