package gov.uk.ets.registry.api.transaction.web.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.accountaccess.service.AccountAccessService;
import gov.uk.ets.registry.api.transaction.domain.AccountProjection;
import gov.uk.ets.registry.api.transaction.domain.TransactionProjection;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.web.model.TransactionSearchResult;
import gov.uk.ets.registry.api.transaction.web.model.TransactionSearchResult.AccountInfo;
import java.util.Date;
import java.util.function.Function;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionSearchResultMapperTest {

  TransactionSearchResultMapper transactionSearchResultMapper;

  @Mock
  AccountProjection transferringAccountProjection;

  @Mock
  AccountProjection acquiringAccountProjection;

  @Mock
  TransactionProjection transactionProjection;

  @Mock
  AccountAccessService accountAccessService;

  @BeforeEach
  void setUp() {
    transactionSearchResultMapper = new TransactionSearchResultMapper(accountAccessService);
  }

  @Test
  void map() {
    // given
    mockTransactionProjection(transactionProjection, MockTransactionProjectionInput
        .builder()
        .identifier("UK-1232133221")
        .lastUpdated(new Date())
        .mockAcquiringAccountInput(MockAccountProjectionInput.builder()
            .accountFullIdentifier("UK-TRANSFERRING-213123")
            .kyotoAccountType(KyotoAccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT)
            .ukRegistryAccountIdentifier(12345L)
            .ukRegistryAccountName("uk-acquiring-registry-account-name")
            .build())
        .mockTransferringAccountInput(MockAccountProjectionInput.builder()
            .accountFullIdentifier("UK-ACQUIRING-213123")
            .kyotoAccountType(KyotoAccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT)
            .ukRegistryAccountIdentifier(12345L)
            .ukRegistryAccountName("uk-transferring-registry-account-name")
            .build())
        .quantity(120L)
        .status(TransactionStatus.COMPLETED)
        .type(TransactionType.TransferToSOPforFirstExtTransferAAU)
        .unitType(UnitType.AAU)
        .build());

    // when
    TransactionSearchResult searchResult = transactionSearchResultMapper.map(transactionProjection);

    // then
    assertEquals(transactionProjection.getIdentifier(), searchResult.getId());
    assertEquals(transactionProjection.getQuantity(), searchResult.getUnits().getQuantity());
    assertEquals(transactionProjection.getUnitType().name(), searchResult.getUnits().getType());
    assertEquals(transactionProjection.getStatus().name(), searchResult.getStatus());
    assertEquals(transactionProjection.getLastUpdated(), searchResult.getLastUpdated());
    assertEquals(transactionProjection.getType().name(), searchResult.getType());
    assertEquals(transactionProjection.getAcquiringAccount().getUkRegistryAccountIdentifier(), searchResult.getAcquiringAccount().getUkRegistryIdentifier());
    assertEquals(transactionProjection.getTransferringAccount().getUkRegistryAccountIdentifier(), searchResult.getTransferringAccount().getUkRegistryIdentifier());

    verifyThatAccountTitleIsCreatedCorrectly(acquiringAccountProjection, sr -> sr.getAcquiringAccount());

    verifyThatAccountTitleIsCreatedCorrectly(transferringAccountProjection, sr -> sr.getTransferringAccount());
  }

  private void verifyThatAccountTitleIsCreatedCorrectly(AccountProjection accountProjection, Function<TransactionSearchResult, AccountInfo> getAccountInfoFunc) {
    // given
    given(accountProjection.isGovernmentAccount()).willReturn(true);
    // when
    TransactionSearchResult searchResult = transactionSearchResultMapper.map(transactionProjection);
    // then
    assertEquals(accountProjection.getUkRegistryAccountName(), getAccountInfoFunc.apply(searchResult).getTitle());
    assertNotEquals(accountProjection.getAccountFullIdentifier(), getAccountInfoFunc.apply(searchResult).getTitle());

    // given
    given(accountProjection.isGovernmentAccount()).willReturn(false);
    // when
    searchResult = transactionSearchResultMapper.map(transactionProjection);
    // then
    assertNotEquals(accountProjection.getUkRegistryAccountName(), getAccountInfoFunc.apply(searchResult).getTitle());
    assertEquals(accountProjection.getAccountFullIdentifier(), getAccountInfoFunc.apply(searchResult).getTitle());
  }

  private TransactionProjection mockTransactionProjection(TransactionProjection transactionProjection, MockTransactionProjectionInput input) {
    mockAccountProjection(transferringAccountProjection, input.mockTransferringAccountInput);
    mockAccountProjection(acquiringAccountProjection, input.mockAcquiringAccountInput);
    when(transactionProjection.getIdentifier()).thenReturn(input.identifier);
    when(transactionProjection.getTransferringAccount()).thenReturn(transferringAccountProjection);
    when(transactionProjection.getAcquiringAccount()).thenReturn(acquiringAccountProjection);
    when(transactionProjection.getLastUpdated()).thenReturn(input.lastUpdated);
    when(transactionProjection.getQuantity()).thenReturn(input.quantity);
    when(transactionProjection.getStatus()).thenReturn(input.status);
    when(transactionProjection.getType()).thenReturn(input.type);
    when(transactionProjection.getUnitType()).thenReturn(input.unitType);
    return transactionProjection;
  }

  private AccountProjection mockAccountProjection(AccountProjection accountProjection, MockAccountProjectionInput input) {
    when(accountAccessService.checkAccountAccess(any())).thenReturn(true);
    when(accountProjection.getAccountFullIdentifier()).thenReturn(input.accountFullIdentifier);
    when(accountProjection.getUkRegistryAccountIdentifier()).thenReturn(input.ukRegistryAccountIdentifier);
    when(accountProjection.getUkRegistryAccountName()).thenReturn(input.ukRegistryAccountName);
    return accountProjection;
  }

  @Builder
  static class MockTransactionProjectionInput {
    private String identifier;
    private TransactionType type;
    private TransactionStatus status;
    private Date lastUpdated;
    private Long quantity;
    private UnitType unitType;
    private MockAccountProjectionInput mockTransferringAccountInput;
    private MockAccountProjectionInput mockAcquiringAccountInput;
  }

  @Builder
  static class MockAccountProjectionInput {
    private String accountFullIdentifier;
    private KyotoAccountType kyotoAccountType;
    private RegistryAccountType registryAccountType;
    private Long ukRegistryAccountIdentifier;
    private String ukRegistryAccountName;
  }
}