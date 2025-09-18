package gov.uk.ets.registry.api.user;

import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.user.domain.UserWorkContact;
import gov.uk.ets.registry.api.user.repository.UserWorkContactRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserWorkContactRepositoryImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AuthorizationService authorizationService;

    private UserWorkContactRepositoryImpl repository;

    @BeforeEach
    public void setup() {
        // Instantiate the real object
        repository = new UserWorkContactRepositoryImpl(
            "http://keycloak.example.com",
            "test-realm",
            "/users",
            restTemplate,
            authorizationService,
            null
        );
        // Spy on the repository to allow partial mocking
        repository = Mockito.spy(repository);
    }

    @Test
    public void testFetchUserWorkContactsInBatches() {
        // Prepare input
        Set<String> urIds = new HashSet<>();
        for (int i = 1; i <= 120; i++) {
            urIds.add("URID" + i);
        }

        // Prepare mock response for fetch
        UserWorkContact mockContact = new UserWorkContact();
        mockContact.setUrid("URID1");
        List<UserWorkContact> mockResponse = Collections.singletonList(mockContact);

        // Mock the fetch method to return the controlled response without invoking the actual method
        doReturn(mockResponse).when(repository).fetch(anySet(), eq(true));

        // Execute the method under test
        List<UserWorkContact> result = repository.fetchUserWorkContactsInBatches(urIds);

        // Assertions
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Adjust based on batching logic (e.g., 3 calls with batch size of 50)
        assertEquals(3, result.size()); // Updated to reflect the batch size of 50 and mock behavior

        // Verify fetch was called 3 times (assuming batch size of 50)
        verify(repository, times(3)).fetch(anySet(), eq(true));
    }

    @Test
    public void testFetchUserWorkContactsInBatches_EmptyInput() {
        List<UserWorkContact> result = repository.fetchUserWorkContactsInBatches(Collections.emptySet());
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, Mockito.never()).fetch(anySet(), eq(true));
    }
}