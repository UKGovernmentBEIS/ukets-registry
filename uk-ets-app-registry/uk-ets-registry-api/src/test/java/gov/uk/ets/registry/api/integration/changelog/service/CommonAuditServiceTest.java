package gov.uk.ets.registry.api.integration.changelog.service;

import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountDTOFactory;
import gov.uk.ets.registry.api.account.web.model.OperatorDTO;
import gov.uk.ets.registry.api.integration.changelog.DomainObject;
import gov.uk.ets.registry.api.integration.changelog.domain.IntegrationChangeLog;
import gov.uk.ets.registry.api.integration.changelog.repository.IntegrationChangeLogRepository;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommonAuditServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountDTOFactory accountDTOFactory;
    @Mock
    private IntegrationChangeLogRepository changeLogRepository;

    private CommonAuditService service;

    @BeforeEach
    public void setup() {
        service = new CommonAuditService(accountRepository, accountDTOFactory, changeLogRepository);
    }

    @Test
    public void testObjectWithDepth() {
        // given
        OperatorDTO oldVersion = createObject("old", 1);
        OperatorDTO newVersion = createObject("new", 2);

        // when
        service.handleChanges(DomainObject.OPERATOR, oldVersion, newVersion,
                1L, "fullIdentifier", 123L, SourceSystem.METSIA_INSTALLATION);

        // then
        verify(changeLogRepository, times(2)).save(any(IntegrationChangeLog.class));

    }

    @Test
    public void testObjectWithoutDepth() {
        // given
        String oldVersion = "old";
        String newVersion = "new";

        // when
        service.handleChanges(DomainObject.EMISSIONS, "emissions", oldVersion, newVersion,
                1L, "fullIdentifier", 123L, SourceSystem.METSIA_INSTALLATION);

        // then
        verify(changeLogRepository, times(1)).save(any(IntegrationChangeLog.class));
    }

    private OperatorDTO createObject(String name, Integer year) {
        OperatorDTO operatorDTO = new OperatorDTO();
        operatorDTO.setName(name);
        operatorDTO.setFirstYear(year);
        operatorDTO.setActivityTypes(Set.of(InstallationActivityType.PRODUCTION_OF_COKE));
        return operatorDTO;
    }
}
