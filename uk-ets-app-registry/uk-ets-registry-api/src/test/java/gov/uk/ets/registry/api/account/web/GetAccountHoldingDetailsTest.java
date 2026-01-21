package gov.uk.ets.registry.api.account.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import gov.uk.ets.registry.api.account.AccountController;
import gov.uk.ets.registry.api.account.service.AccountClaimService;
import gov.uk.ets.registry.api.account.service.AccountOperatorUpdateService;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.validation.AccountValidator;
import gov.uk.ets.registry.api.account.web.model.AccountHoldingDetailsCriteria;
import gov.uk.ets.registry.api.account.web.model.AccountHoldingDetailsDTO;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.web.mapper.TransactionSearchResultMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
public class GetAccountHoldingDetailsTest {

    @Mock
    AccountService accountService;
    @Mock
    AuthorizationService authService;
    @MockBean
    private AccountValidator accountValidator;
    @Mock
    private AccountOperatorUpdateService accountOperatorUpdateService;
    @MockBean
    private TransactionSearchResultMapper transactionSearchResultMapper;
    @MockBean
    private AccountClaimService accountClaimService;

    AccountController controller;

    @BeforeEach
    public void setup() {
        controller = new AccountController(accountService, authService, accountValidator,
                                           accountOperatorUpdateService, transactionSearchResultMapper, accountClaimService);
    }

    @Test
    public void testRegistryAdministrators() {
        // given
        UnitBlock unitBlock = new UnitBlock();
        unitBlock.setAccountIdentifier(12345L);
        unitBlock.setReservedForTransaction(null);
        unitBlock.setStartBlock(1000L);
        unitBlock.setEndBlock(1030L);
        unitBlock.setApplicablePeriod(CommitmentPeriod.CP0);
        unitBlock.setOriginalPeriod(CommitmentPeriod.CP2);
        unitBlock.setType(UnitType.CER);
        given(accountService.getUnitBlocks(any())).willReturn(List.of(unitBlock));
        given(authService.hasScopePermission(any())).willReturn(true);

        AccountHoldingDetailsCriteria criteria = new AccountHoldingDetailsCriteria();
        criteria.setAccountId(unitBlock.getAccountIdentifier());
        criteria.setUnit(unitBlock.getType().name());
        criteria.setApplicablePeriodCode(unitBlock.getApplicablePeriod().getCode());
        criteria.setOriginalPeriodCode(unitBlock.getOriginalPeriod().getCode());

        // when
        AccountHoldingDetailsDTO dto = controller.getAccountHoldingDetails(criteria);

        // then
        assertEquals(unitBlock.getType().name(), dto.getUnit());
        assertEquals(unitBlock.getApplicablePeriod().name(), dto.getApplicablePeriod());
        assertEquals(unitBlock.getOriginalPeriod().name(), dto.getOriginalPeriod());
        assertNotNull(dto.getResults());
        assertEquals(1, dto.getResults().size());

        assertEquals(unitBlock.getProjectNumber(), dto.getResults().get(0).getProject());
        assertEquals(unitBlock.getEndBlock(), dto.getResults().get(0).getSerialNumberEnd());
        assertEquals(unitBlock.getStartBlock(), dto.getResults().get(0).getSerialNumberStart());
        assertEquals(unitBlock.getEndBlock() - unitBlock.getStartBlock() + 1, dto.getResults().get(0).getQuantity());
        assertEquals(unitBlock.getReservedForTransaction() != null, dto.getResults().get(0).isReserved());
    }

    @Test
    public void testEnrolledNonAdmin() {
        // given
        UnitBlock unitBlock = new UnitBlock();
        unitBlock.setAccountIdentifier(12345L);
        unitBlock.setReservedForTransaction(null);
        unitBlock.setStartBlock(1000L);
        unitBlock.setEndBlock(1030L);
        unitBlock.setApplicablePeriod(CommitmentPeriod.CP0);
        unitBlock.setOriginalPeriod(CommitmentPeriod.CP2);
        unitBlock.setType(UnitType.CER);
        given(accountService.getUnitBlocks(any())).willReturn(List.of(unitBlock));
        given(authService.hasScopePermission(any())).willReturn(false);
        final Long quantity = unitBlock.getEndBlock() - unitBlock.getStartBlock() + 1;

        AccountHoldingDetailsCriteria criteria = new AccountHoldingDetailsCriteria();
        criteria.setAccountId(unitBlock.getAccountIdentifier());
        criteria.setUnit(unitBlock.getType().name());
        criteria.setApplicablePeriodCode(unitBlock.getApplicablePeriod().getCode());
        criteria.setOriginalPeriodCode(unitBlock.getOriginalPeriod().getCode());

        // when
        AccountHoldingDetailsDTO dto = controller.getAccountHoldingDetails(criteria);

        // then
        assertEquals(unitBlock.getType().name(), dto.getUnit());
        assertEquals(unitBlock.getApplicablePeriod().name(), dto.getApplicablePeriod());
        assertEquals(unitBlock.getOriginalPeriod().name(), dto.getOriginalPeriod());
        assertNotNull(dto.getResults());
        assertEquals(1, dto.getResults().size());
        assertEquals(unitBlock.getProjectNumber(), dto.getResults().get(0).getProject());
        assertNull(dto.getResults().get(0).getSerialNumberEnd());
        assertNull(dto.getResults().get(0).getSerialNumberStart());
        assertEquals(quantity, dto.getResults().get(0).getQuantity());
        assertEquals(unitBlock.getReservedForTransaction() != null, dto.getResults().get(0).isReserved());
    }


}
