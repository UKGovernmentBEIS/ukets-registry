package gov.uk.ets.registry.api.user;

import static gov.uk.ets.registry.api.user.domain.UserStatus.DEACTIVATED;
import static gov.uk.ets.registry.api.user.domain.UserStatus.DEACTIVATION_PENDING;
import static gov.uk.ets.registry.api.user.domain.UserStatus.SUSPENDED;
import static java.util.Collections.frequency;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.ar.domain.ARAccountAccessRepository;
import gov.uk.ets.registry.api.ar.infrastructure.ARAccountAccessRepositoryImpl;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create"
})
@Sql({"/test-data/search_ars_by_account_holder.sql"})
class GetAccountHoldersTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private ARAccountAccessRepository arAccountAccessRepository;

    private final long accountHolderId = 1000003L;

    @BeforeEach
    public void setup() {
        arAccountAccessRepository = new ARAccountAccessRepositoryImpl(entityManager);
    }

    @Test
    @DisplayName("Get AR's when Administrator will return all available AR's for Account Holder only")
    public void testRegistryAdministrator() {

        var filteredOutStatuses = Arrays.asList(SUSPENDED, DEACTIVATED, DEACTIVATION_PENDING);

        // senior registry administrator
        var admin = userRepository.findByUrid("UK802061511788");
        Assertions.assertNotNull(admin, "urid: UK802061511788 not found in db");
        assertEquals(
            "Registry Administrator", admin.getFirstName(), "UK802061511788 not Registry Administrator"
        );

        var accountHolderArs = userRepository.getAccountHolderArs(accountHolderId, null);
        assertEquals(2, accountHolderArs.size(), "Authorised representatives for account holder not expected size");

        var statusNotInFilteredOutList = accountHolderArs
            .stream()
            .map(UserDTO::getStatus)
            .noneMatch(filteredOutStatuses::contains);
        assertTrue(statusNotInFilteredOutList, "Invalid status found");

        var userIds = accountHolderArs.stream()
            .map(UserDTO::getUrid).toList();

        var adminUserNotIncluded = accountHolderArs.stream()
            .map(UserDTO::getUrid)
            .noneMatch(i -> i.equals(admin.getUrid()));
        assertTrue(adminUserNotIncluded, "Admin user found in query");

        var noDuplicatesFound = userIds.stream().noneMatch(i -> frequency(userIds, i) > 1);
        assertTrue(noDuplicatesFound, "Admin user found in query");

        var newAcccountHolderAr = userRepository.getAccountHolderArs(null, null);
        assertEquals(0, newAcccountHolderAr.size(), "Authorised representative for null account holder not expected size");
    }

    @Test
    @DisplayName("Get AR's when Non-Administrator will return only associated/known AR's")
    public void testNonAdministrator() {

        // Representative 2
        var expectedAssociatedArUid = "UK588332110438";

        // Representative 1
        var ar1 = userRepository.findByUrid("UK405681794859");
        Assertions.assertNotNull(ar1, "urid: UK405681794859 not found in db");
        assertEquals(ar1.getFirstName(), "Representative 1", "UK405681794859 not Representative 1");

        var accountHolderArs = userRepository.getAccountHolderArs(accountHolderId, ar1.getId());
        assertEquals(3, accountHolderArs.size(),"Authorised representatives not expected size");
        String foundUserId = accountHolderArs.stream().map(UserDTO::getUrid).findFirst().orElseThrow(RuntimeException::new);
        assertEquals(expectedAssociatedArUid, foundUserId, "Expected uid not found");


        var rep1 = "UK405681794859";
        var rep2 = "UK588332110438";
        var account50 = 10000060L;
        var account51 = 10000061L;

        //must find user id 15 and 2
        var rep1Account50 = arAccountAccessRepository.fetchArsForAccount(account50, rep1);
        assertEquals(2, rep1Account50.size(),"Authorised representatives not expected size");
        assertTrue(
            rep1Account50.stream().map(a -> a.getUser().getId()).anyMatch(id -> List.of(2L, 15L).contains(id))
        );
        assertFalse(rep1Account50.stream().anyMatch(accountAccess -> accountAccess.getUser().getUrid().equals(rep1)));

        //must find only user id 14
        var rep1Account51 = arAccountAccessRepository.fetchArsForAccount(account51, rep1);
        assertEquals(1, rep1Account51.size(),"Authorised representatives not expected size");
        assertTrue(rep1Account51.stream().map(a -> a.getUser().getId()).anyMatch(id -> id == 14L));
        assertFalse(rep1Account51.stream().anyMatch(accountAccess -> accountAccess.getUser().getUrid().equals(rep1)));

        //must find no users
        var rep2account5 = arAccountAccessRepository.fetchArsForAccount(account51, rep2);
        assertEquals(0, rep2account5.size(),"Authorised representatives not expected size");


    }
}
